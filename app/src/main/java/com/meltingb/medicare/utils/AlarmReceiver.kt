package com.meltingb.medicare.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.meltingb.medicare.R
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.view.MainActivity
import java.util.*

/**
 * 설정 > 점심 알림 설정
 */
class AlarmReceiver : BroadcastReceiver() {

    private lateinit var builder: NotificationCompat.Builder

    companion object {
        const val CHANNEL_ID = "medicare"
        const val CHANNEL_NAME = "medicare"
        const val CHANNEL_DESCRIPTION = "약 복용 알람"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra("bundle")
        val days = bundle!!.getBooleanArray("notify_days") // 반복할 요일
        val alarm = bundle.getParcelable<PillEntity>("alarm")

        if (alarm != null) setAlarm(context, alarm)

        val cal: Calendar = Calendar.getInstance()
        if (!days!![cal.get(Calendar.DAY_OF_WEEK) - 1]) return

        val notificationManager: NotificationManager? = context!!.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager!!.createNotificationChannel(channel)
        }
        builder = NotificationCompat.Builder(context, CHANNEL_ID)

        val intent2 = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, AppPreference.get(PREF_KEY_LAST_ID, 0), intent2, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setContentTitle("${alarm!!.pillName} 복용 시간")
        builder.setContentText("${alarm.pillName} 복용 후 앱에서 복용 체크를 해주세요!!")
        builder.color = Color.parseColor("#F4674C")
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)
        val notify = builder.build()
        notificationManager!!.notify(1, notify)

    }

    private fun setAlarm(context: Context, alarm: PillEntity) {
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
        val pendingIntent = PendingIntent.getBroadcast(context, alarm.alarmId, intent, 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectTime, pendingIntent)
    }

}