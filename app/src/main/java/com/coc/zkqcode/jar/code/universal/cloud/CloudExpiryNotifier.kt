package com.coc.zkqcode.jar.code.universal.cloud

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.jar.code.universal.push.PushMessage
import com.coc.zkqcode.jar.code.universal.push.PushRouter
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.google.gson.JsonObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val cloudExpiryPath = "${Environment.getExternalStorageDirectory().path}/zkqFiles/cloud_expiry.json"

private suspend fun readCloudExpiryFile(): JsonObject {
    return FileHelper.readJson(cloudExpiryPath) ?: JsonObject()
}

private suspend fun writeCloudExpiryFile(obj: JsonObject): Boolean {
    return FileHelper.writeJson(cloudExpiryPath, obj.toString())
}

/** Effective reset days: file (last auto reset) overrides config, clamped to >= 1. */
private suspend fun readResetDays(): Int {
    val fileResetDays = readCloudExpiryFile().get("lastResetDays")?.asInt
    val cfgResetDays = GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_RESET_DAYS.key]?.value?.toIntOrNull() ?: 30
    return (fileResetDays ?: cfgResetDays).coerceAtLeast(1)
}

/** Effective expiry date: file (auto-reset result) overrides config. Null if unset/invalid. */
private suspend fun effectiveExpireDate(): LocalDate? {
    val fileExpireDate = readCloudExpiryFile().get("lastExpireDate")?.asString.orEmpty()
    val dateStr = fileExpireDate.ifBlank {
        GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key]?.value.orEmpty()
    }
    if (dateStr.isBlank()) return null
    return try {
        LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: Exception) {
        null
    }
}

/**
 * Rolls the expiry date forward by reset days (shared by auto reset and manual reset).
 * Persists to the dedicated file and syncs the in-memory config so the UI refreshes.
 * Returns the new date string, or null when the reset is pointless (new date still in the past).
 */
private suspend fun performReset(expireDate: LocalDate): String? {
    val today = LocalDate.now()
    val resetDays = readResetDays()
    val newExpireDate = expireDate.plusDays(resetDays.toLong())
    // Guard: if the new date is still in the past, reset is pointless — skip to avoid notify loops
    if (!newExpireDate.isAfter(today)) return null
    val newDateStr = newExpireDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

    val obj = JsonObject()
    obj.addProperty("lastExpireDate", newDateStr)
    obj.addProperty("lastResetDays", resetDays)
    obj.addProperty("lastNotifyMin", System.currentTimeMillis() / 60_000)
    writeCloudExpiryFile(obj)

    // Sync in-memory config so the UI shows the new date immediately (disk config untouched;
    // the file keeps the authoritative value across restarts).
    GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key]?.value = newDateStr
    return newDateStr
}

/**
 * Manual reset triggered from the settings UI.
 * Restores the initial state: clears the configured expiry date and the persisted
 * auto-reset state. Reset days / advance days settings are kept untouched.
 * Returns true on success.
 */
suspend fun manualResetCloudExpiry(): Boolean {
    GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key]?.value = ""
    return FileHelper.deleteJson(cloudExpiryPath)
}

/**
 * Called after configs are saved. If the user changed the expiry date or reset days
 * (config differs from the persisted auto-reset state), drop the persisted auto-reset
 * state so the user's config takes effect. The 24h notify cooldown is kept.
 */
suspend fun clearCloudExpiryIfUserChanged() {
    val file = readCloudExpiryFile()
    val fileExpireDate = file.get("lastExpireDate")?.asString.orEmpty()
    val fileResetDays = file.get("lastResetDays")?.asInt
    if (fileExpireDate.isEmpty() && fileResetDays == null) return // nothing persisted

    val cfgDate = GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key]?.value.orEmpty()
    val cfgResetDays = GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_RESET_DAYS.key]?.value?.toIntOrNull()
    val dateChanged = fileExpireDate.isNotEmpty() && cfgDate != fileExpireDate
    val resetChanged = fileResetDays != null && cfgResetDays != null && cfgResetDays != fileResetDays
    if (!dateChanged && !resetChanged) return

    file.remove("lastExpireDate")
    file.remove("lastResetDays")
    writeCloudExpiryFile(file)
}

suspend fun checkCloudExpiry() {
    if (GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_NOTIFY.key]?.value != "1") return

    val deviceRemark = GlobalVars.configStates[GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()
    val nowMin = System.currentTimeMillis() / 60_000
    val lastNotifyMin = readCloudExpiryFile().get("lastNotifyMin")?.asLong ?: 0L

    val expireDate = effectiveExpireDate()
    if (expireDate == null) {
        // Notify enabled but no date configured → remind the user to set one (24h cooldown)
        if (nowMin - lastNotifyMin < 1440) return
        val obj = readCloudExpiryFile()
        obj.addProperty("lastNotifyMin", nowMin)
        writeCloudExpiryFile(obj)
        PushRouter.send(PushMessage(
            title = Strings.cloudExpireNoDateTitle + deviceRemark.ifBlank { "无" },
            desp = Strings.cloudExpireNoDateMsg(deviceRemark)
        ))
        return
    }

    val advanceDays = GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_ADVANCE_DAYS.key]?.value?.toIntOrNull() ?: 3
    val today = LocalDate.now()
    val daysLeft = today.until(expireDate).days

    if (daysLeft < 0) {
        // Expired → auto reset only when the auto-reset switch is on
        val autoResetOn = GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_AUTO_RESET.key]?.value == "1"
        if (!autoResetOn) return
        performReset(expireDate)?.let { newDateStr ->
            PushRouter.send(PushMessage(
                title = Strings.cloudExpireResetTitle + deviceRemark.ifBlank { "无" },
                desp = Strings.cloudExpireResetMsg(deviceRemark, readResetDays(), newDateStr)
            ))
        }
    } else if (daysLeft <= advanceDays) {
        // Within advance window → 24h cooldown
        val nowMin = System.currentTimeMillis() / 60_000
        if (nowMin - lastNotifyMin < 1440) return
        val obj = readCloudExpiryFile()
        obj.addProperty("lastNotifyMin", nowMin)
        writeCloudExpiryFile(obj)

        PushRouter.send(PushMessage(
            title = Strings.cloudExpireExpiringTitle(deviceRemark, daysLeft),
            desp = Strings.cloudExpireExpiringMsg(deviceRemark, daysLeft, expireDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
        ))
    }
}
