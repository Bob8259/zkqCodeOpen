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

suspend fun checkCloudExpiry() {
    if (GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_NOTIFY.key]?.value != "1") return

    // Read persisted data from dedicated file (survives memory clear)
    val file = readCloudExpiryFile()
    val fileExpireDate = file.get("lastExpireDate")?.asString.orEmpty()
    val fileResetDays = file.get("lastResetDays")?.asInt
    val lastNotifyMin = file.get("lastNotifyMin")?.asLong ?: 0L

    // Effective date: file overrides config (auto-reset writes to file only)
    val dateStr = fileExpireDate.ifBlank {
        GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key]?.value.orEmpty()
    }
    if (dateStr.isBlank()) return
    val expireDate = try { LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE) } catch (_: Exception) { return }

    val advanceDays = GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_ADVANCE_DAYS.key]?.value?.toIntOrNull() ?: 3
    val resetDays = fileResetDays ?: (GlobalVars.configStates[GLOBAL_SETTINGS.CLOUD_EXPIRE_RESET_DAYS.key]?.value?.toIntOrNull() ?: 30)

    val today = LocalDate.now()
    val daysLeft = today.until(expireDate).days
    val deviceRemark = GlobalVars.configStates[GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()

    val nowMin = System.currentTimeMillis() / 60_000

    if (daysLeft < 0) {
        // Expired → auto reset, write to dedicated file only
        val newExpireDate = expireDate.plusDays(resetDays.toLong())
        val newDateStr = newExpireDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val obj = JsonObject()
        obj.addProperty("lastExpireDate", newDateStr)
        obj.addProperty("lastResetDays", resetDays)
        obj.addProperty("lastNotifyMin", nowMin)
        writeCloudExpiryFile(obj)

        PushRouter.send(PushMessage(
            title = Strings.cloudExpireResetTitle + deviceRemark.ifBlank { "无" },
            desp = Strings.cloudExpireResetMsg(deviceRemark, resetDays, newDateStr)
        ))
    } else if (daysLeft <= advanceDays) {
        // Within advance window → 24h cooldown
        if (nowMin - lastNotifyMin < 1440) return
        val obj = readCloudExpiryFile()
        obj.addProperty("lastNotifyMin", nowMin)
        writeCloudExpiryFile(obj)

        PushRouter.send(PushMessage(
            title = Strings.cloudExpireExpiringTitle(deviceRemark, daysLeft),
            desp = Strings.cloudExpireExpiringMsg(deviceRemark, daysLeft, dateStr)
        ))
    }
}
