package com.coc.zkqcode.jar.code.universal.ctrl

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.loadSchedule
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.scheduling.resolveEnabledAccountIds
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import org.json.JSONObject
import java.util.UUID

/**
 * Builds the read-only status snapshot pushed to the AstrBot plugin (doc §13.1).
 * All data is read from in-process state; no inbound listener involved.
 */
object StatusSnapshot {

    private const val DEVICE_ID_KEY = "zkq_device_id"

    // Name conflict override (doc §13.1): when the plugin rejects our deviceId
    // as belonging to a genuinely different device (same name, different nonce),
    // we auto-rename to "<name>-2", "-3"... and persist the final name here.
    // Takes precedence over DEVICE_REMARK so the rename survives restarts.
    private const val DEVICE_ID_OVERRIDE_KEY = "zkq_device_id_override"

    /** Current scheduling mode: 升级时间上号 / 随机上号 / 顺序模式. */
    fun mode(): String {
        val isUpgrade = GlobalVars.configStates[GLOBAL_SETTINGS.UPGRADE_TIME_SCHEDULING.key]?.value == "1"
        val isRandom = GlobalVars.configStates[GLOBAL_SETTINGS.RANDOM_SCHEDULING.key]?.value == "1"
        return when {
            isUpgrade -> "升级时间上号"
            isRandom -> "随机上号"
            else -> "顺序模式"
        }
    }

    /**
     * Stable device id: override (after a name conflict) > DEVICE_REMARK >
     * persisted per-install random id (unique per device, survives restarts).
     * When the user changes the remark, a stale override whose base name no
     * longer matches is dropped so the new remark takes effect immediately
     * (rename = the plugin cleans up the old name on its side).
     */
    suspend fun deviceId(): String {
        val override = readMemory(DEVICE_ID_OVERRIDE_KEY)
        val remark = GlobalVars.configStates[GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()
        if (remark.isNotBlank()) {
            if (override.isNotBlank()) {
                val base = override.replace(Regex("-\\d+$"), "")
                if (base != remark) {
                    writeMemory(DEVICE_ID_OVERRIDE_KEY, "")
                    return remark
                }
                return override
            }
            return remark
        }
        val stored = readMemory(DEVICE_ID_KEY)
        if (stored.isNotBlank()) return stored
        val generated = "device-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12)
        writeMemory(DEVICE_ID_KEY, generated)
        return generated
    }

    /**
     * Renames this device after a name-conflict rejection (plugin 409
     * device_conflict): "<name>-2", "<name>-3"... persisted as override.
     */
    suspend fun bumpDeviceIdOnConflict() {
        val current = deviceId()
        val remark = GlobalVars.configStates[GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()
        // When the name is the user's own remark, keep it whole ("手机-2" -> "手机-2-2");
        // only strip the "-N" suffix when the name is one of our own conflict renames.
        val fromRemark = remark.isNotBlank() && current == remark
        val m = if (fromRemark) null else Regex("-(\\d+)$").find(current)
        val base = if (m != null) current.substring(0, m.range.first) else current
        val nextSuffix = if (m != null) ((m.groupValues[1].toIntOrNull() ?: 1) + 1) else 1
        writeMemory(DEVICE_ID_OVERRIDE_KEY, "$base-$nextSuffix")
    }

    /**
     * Builds the snapshot JSON (doc §13.1 fields). nextUpgrade carries an
     * absolute completionTs so the plugin can compute real-time remaining time.
     */
    suspend fun build(lastEvent: String?): JSONObject {
        val schedule = loadSchedule()
        val enabled = resolveEnabledAccountIds()
        val earliest = schedule.filter { it.account in enabled }.minByOrNull { it.completionTs }
        val accountRemark = GlobalVars.configStates["remark${InGameVars.currentAccountNumber}"]?.value.orEmpty()
        return JSONObject().apply {
            put("mode", mode())
            put("running", GlobalVars.isAutoRunEnabled)
            put("currentAccount", InGameVars.currentAccountNumber)
            if (accountRemark.isNotBlank()) put("currentAccountRemark", accountRemark)
            if (earliest != null) {
                put("nextUpgrade", JSONObject().apply {
                    put("account", earliest.account)
                    put("completionTs", earliest.completionTs)
                })
            } else {
                put("nextUpgrade", JSONObject.NULL)
            }
            put("scheduleCount", schedule.size)
            if (!lastEvent.isNullOrBlank()) put("lastEvent", lastEvent)
        }
    }
}
