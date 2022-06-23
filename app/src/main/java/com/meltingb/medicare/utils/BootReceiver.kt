package com.meltingb.medicare.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.meltingb.medicare.data.PillEntity
import org.joda.time.format.ISODateTimeFormat.hour
import java.util.*


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            val pillList = AppPreference.getObjectList(PREF_KEY_PILL_LIST, PillEntity::class.java) ?: emptyList<PillEntity>()
            if (pillList.isNotEmpty()) {
                pillList.forEach {
                    initAlarm(context, it)
                }
            }
        }
    }

    private fun initAlarm(context: Context, alarm: PillEntity) {
        val lastID = AppPreference.get(PREF_KEY_LAST_ID, 0)
        val now = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, alarm.alarmTime.substring(0, 2).toInt())
            set(Calendar.MINUTE, alarm.alarmTime.substring(3, 5).toInt())
        }
        val intervalDay = 24 * 60 * 60 * 1000.toLong() // 24시간
        var selectTime: Long = now.timeInMillis
        if (System.currentTimeMillis() > selectTime) {
            selectTime += intervalDay
        }

        val bundle = Bundle()
        bundle.putBooleanArray("notify_days", alarm.alarmWeek.toBooleanArray())
        bundle.putParcelable("alarm", alarm)
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("bundle", bundle)
        val pendingIntent = PendingIntent.getBroadcast(context, lastID + 1, intent, 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, selectTime, AlarmManager.INTERVAL_DAY, pendingIntent)

        AppPreference.set(PREF_KEY_LAST_ID, lastID + 1)
    }
}