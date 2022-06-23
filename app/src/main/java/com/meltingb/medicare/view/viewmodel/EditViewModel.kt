package com.meltingb.medicare.view.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.meltingb.medicare.R
import com.meltingb.medicare.core.BaseViewModel
import com.meltingb.medicare.core.SingleLiveEvent
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.db.dao.PillDao
import com.meltingb.medicare.db.repo.PillRepository
import com.meltingb.medicare.utils.*
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.CoroutineContext

class EditViewModel(
    var pillRepo: PillRepository
) : BaseViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    val navigationLiveData: SingleLiveEvent<NavigationEvent> get() = _navigationLiveData
    private val _navigationLiveData = SingleLiveEvent<NavigationEvent>()

    val pillImgNumLiveData = MutableLiveData<Int>()
    val pillImgNum: Int get() = pillImgNumLiveData.value ?: 0
    val pillNameLiveData = MutableLiveData<String>()
    val pillName: String get() = pillNameLiveData.value ?: ""
    val takeNumLiveData = MutableLiveData<String>()
    val takeNum: String get() = takeNumLiveData.value ?: ""
    val takeTypeLiveData = MutableLiveData<String>()
    val takeType: String get() = takeTypeLiveData.value ?: ""
    val groupIDLiveData = MutableLiveData<String>()
    val groupID: String get() = groupIDLiveData.value ?: ""
    val alarmIDLiveData = MutableLiveData<Int>()
    val alarmID: Int get() = alarmIDLiveData.value ?: 0
    val alarmDayListLiveData = MutableLiveData<List<Boolean>>()
    val alarmTimeListLiveData = MutableLiveData<List<String>>()

    val alarmTime1Visible = MutableLiveData(View.GONE)
    val pillIDLiveData = MutableLiveData<Int>()
    val pillID: Int? get() = pillIDLiveData.value

    val timeHours1LiveData = MutableLiveData<String>("09")
    val timeMinutes1LiveData = MutableLiveData<String>("00")

    val sunDayChecked = MutableLiveData(true)
    val monDayChecked = MutableLiveData(true)
    val tueDayChecked = MutableLiveData(true)
    val wedDayChecked = MutableLiveData(true)
    val thuDayChecked = MutableLiveData(true)
    val friDayChecked = MutableLiveData(true)
    val satDayChecked = MutableLiveData(true)

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    fun moveHome() {
        _navigationLiveData.postValue(NavigationEvent.HomeView)
    }

    @SuppressLint("CheckResult")
    fun addAlarm(view: View) {
        var now = DateTimeFormat.forPattern("MMddHHmm").print(DateTime.now())
        val alarmDayList = listOf<Boolean>(
            sunDayChecked.value!!,
            monDayChecked.value!!,
            tueDayChecked.value!!,
            wedDayChecked.value!!,
            thuDayChecked.value!!,
            friDayChecked.value!!,
            satDayChecked.value!!
        )

        val alarmTimeList = mutableListOf("${timeHours1LiveData.value}:${timeMinutes1LiveData.value}")

        if (isValidAlarm(alarmTimeList)) {
            alarmTimeList.forEach {
                val pillEntity = PillEntity(
                    pillID,
                    groupID,
                    pillName = pillName,
                    takeNum = takeNum,
                    takeType = takeType,
                    alarmWeek = alarmDayList,
                    alarmTime = it,
                    pillImageNum = pillImgNum,
                    createAt = dateTimeFormatter.print(DateTime.now()),
                    takeDayList = listOf(),
                    alarmId = alarmID
                )

                pillRepo.insertPill(pillEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        moveHome()
                    }, {})
                initAlarm(view, pillEntity)
            }
        }
    }

    private fun isValidAlarm(timeList: List<String>): Boolean {
        when {
            pillName.isEmpty() -> {
                return false
            }
            takeNum.isEmpty() -> {
                return false
            }
            takeType.isEmpty() -> {
                return false
            }
            timeList.isEmpty() -> {
                return false
            }
        }
        return true
    }

    private fun initAlarm(view: View, alarm: PillEntity) {
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
        val intent = Intent(view.context, AlarmReceiver::class.java)
        intent.putExtra("bundle", bundle)
        val pendingIntent = PendingIntent.getBroadcast(view.context, lastID + 1, intent, 0)

        val alarmManager = view.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, selectTime, AlarmManager.INTERVAL_DAY, pendingIntent)

        AppPreference.set(PREF_KEY_LAST_ID, lastID + 1)
    }

    private fun deletePill(context: Context) {
        if (pillID != null) {
            pillRepo.deletePill(pillID!!) { isSuccess ->
                if (isSuccess) {
                    deleteAlarm(context, alarmID)
                    moveHome()
                } else {
                    showDeleteFailDialog(context)
                }
            }
        }
    }

    private fun deleteAlarm(context: Context, alarmID: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmID, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun showDeleteDialog(view: View) {
        val dialog = NoTitleDialogBuilder(view.context, R.layout.dialog_common)
        dialog.initView(R.id.tv_message, R.id.btn_cancel, R.id.btn_confirm)
        dialog.setMessage(view.context.getString(R.string.delete_message))
        dialog.setLeftButton(view.context.getString(R.string.cancel)) {
            dialog.dismiss()
        }
        dialog.setRightButton(view.context.getString(R.string.delete)) {
            dialog.dismiss()
            deletePill(view.context)
        }
        dialog.create()
        dialog.show(0.8)
    }

    private fun showDeleteFailDialog(context: Context) {
        val dialog = NoTitleDialogBuilder(context, R.layout.dialog_common)
        dialog.initView(R.id.tv_message, R.id.btn_cancel, R.id.btn_confirm)
        dialog.setMessage(context.getString(R.string.delete_fail_message))
        dialog.setLeftButton(context.getString(R.string.cancel)) {
            dialog.dismiss()
        }
        dialog.setRightButton(context.getString(R.string.confirm)) {
            dialog.dismiss()
        }
        dialog.create()
        dialog.show(0.8)
    }


}