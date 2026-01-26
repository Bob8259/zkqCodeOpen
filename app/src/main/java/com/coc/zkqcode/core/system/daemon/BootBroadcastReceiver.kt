package com.coc.zkqcode.utils.daemon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.coc.zkqcode.MainActivity

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
             val serviceIntent = Intent(context, DaemonService::class.java)
             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                 context.startForegroundService(serviceIntent)
             } else {
                 context.startService(serviceIntent)
             }
        }
    }
}