package com.meltingb.medicare.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.meltingb.medicare.R
import java.text.SimpleDateFormat
import java.util.*

object Common {

    fun showRequestPermission(context: Context) {
        val dialog = NoTitleDialogBuilder(context, R.layout.dialog_common)
        dialog.initView(R.id.tv_message, R.id.btn_cancel, R.id.btn_confirm)
        dialog.setMessage(context.getString(R.string.permission_request_message))
        dialog.setLeftButton(context.getString(R.string.cancel)) {
            dialog.dismiss()
        }
        dialog.setRightButton(context.getString(R.string.settings)) {
            dialog.dismiss()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        dialog.create()
        dialog.show(0.8)
    }

    fun createID(pattern: String): Int {
        val now = Date()
        return SimpleDateFormat(pattern, Locale.KOREA).format(now).toInt()
    }

}