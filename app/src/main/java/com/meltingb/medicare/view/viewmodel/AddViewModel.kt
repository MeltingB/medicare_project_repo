package com.meltingb.medicare.view.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.bumptech.glide.util.Util
import com.meltingb.medicare.R
import com.meltingb.medicare.core.BaseViewModel
import com.meltingb.medicare.core.SingleLiveEvent
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.db.dao.PillDao
import com.meltingb.medicare.db.repo.PillRepository
import com.meltingb.medicare.utils.*
import com.meltingb.medicare.utils.Common.createID
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

class AddViewModel(
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
    val alarmDayListLiveData = MutableLiveData<List<Boolean>>()
    val alarmTimeListLiveData = MutableLiveData<List<String>>()

    val alarmTime1Visible = MutableLiveData(View.GONE)
    val alarmTime2Visible = MutableLiveData(View.GONE)
    val alarmTime3Visible = MutableLiveData(View.GONE)
    val deleteTime1Visible = MutableLiveData(View.VISIBLE)
    val deleteTime2Visible = MutableLiveData(View.GONE)
    val deleteTime3Visible = MutableLiveData(View.GONE)
    val btnAddVisible = MutableLiveData(View.VISIBLE)

    val timeHours1LiveData = MutableLiveData<String>("09")
    val timeMinutes1LiveData = MutableLiveData<String>("00")
    val timeHours2LiveData = MutableLiveData<String>("09")
    val timeMinutes2LiveData = MutableLiveData<String>("00")
    val timeHours3LiveData = MutableLiveData<String>("09")
    val timeMinutes3LiveData = MutableLiveData<String>("00")
    var alarmCount = 0

    val sunDayChecked = MutableLiveData(true)
    val monDayChecked = MutableLiveData(true)
    val tueDayChecked = MutableLiveData(true)
    val wedDayChecked = MutableLiveData(true)
    val thuDayChecked = MutableLiveData(true)
    val friDayChecked = MutableLiveData(true)
    val satDayChecked = MutableLiveData(true)

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
    fun moveHome(view: View) {
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

        var alarmTimeList = mutableListOf<String>()
        when (alarmCount) {
            1 -> alarmTimeList = mutableListOf("${timeHours1LiveData.value}:${timeMinutes1LiveData.value}")
            2 -> alarmTimeList = mutableListOf(
                "${timeHours1LiveData.value}:${timeMinutes1LiveData.value}",
                "${timeHours2LiveData.value}:${timeMinutes2LiveData.value}"
            )

            3 -> alarmTimeList = mutableListOf(
                "${timeHours1LiveData.value}:${timeMinutes1LiveData.value}",
                "${timeHours2LiveData.value}:${timeMinutes2LiveData.value}",
                "${timeHours3LiveData.value}:${timeMinutes3LiveData.value}"
            )
        }
        if (isValidAlarm(alarmTimeList)) {
            alarmTimeList.forEach {
                val alarmId = AppPreference.get(PREF_KEY_LAST_ID, 0)
                val pillEntity = PillEntity(
                    null,
                    pillGroupID = "${pillName}_${createID("MMdd")}",
                    pillName = pillName,
                    takeNum = takeNum,
                    takeType = takeType,
                    alarmWeek = alarmDayList,
                    alarmTime = it,
                    pillImageNum = pillImgNum,
                    createAt = dateTimeFormatter.print(DateTime.now()),
                    takeDayList = listOf(),
                    alarmId = alarmId
                )
                // TODO: DB 저장
                pillRepo.insertPill(pillEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        moveHome(view)
                        Log.d("insert Db", "success")
                    }, {})
                AppPreference.set(PREF_KEY_LAST_ID, alarmId + 1)
                initAlarm(view, pillEntity)
            }
        }
    }

    fun addTime() {
        when {
            alarmTime1Visible.value != View.VISIBLE -> alarmTime1Visible.postValue(View.VISIBLE)
            alarmTime2Visible.value != View.VISIBLE -> {
                alarmTime2Visible.postValue(View.VISIBLE)
                deleteTime2Visible.postValue(View.VISIBLE)
                deleteTime1Visible.postValue(View.GONE)
            }
            alarmTime3Visible.value != View.VISIBLE -> {
                alarmTime3Visible.postValue(View.VISIBLE)
                deleteTime3Visible.postValue(View.VISIBLE)
                deleteTime2Visible.postValue(View.GONE)
            }
            else -> btnAddVisible.postValue(View.GONE)
        }
        alarmCount += 1
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
        val pendingIntent = PendingIntent.getBroadcast(view.context, alarm.alarmId, intent, 0)

        val alarmManager = view.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectTime, pendingIntent)
    }

    fun deleteTime(index: Int) {
        when (index) {
            1 -> {
                timeHours1LiveData.postValue("09")
                timeMinutes1LiveData.postValue("00")
                alarmTime1Visible.postValue(View.GONE)
                alarmCount = 0
            }
            2 -> {
                timeHours2LiveData.postValue("09")
                timeMinutes2LiveData.postValue("00")
                alarmTime2Visible.postValue(View.GONE)
                deleteTime1Visible.postValue(View.VISIBLE)
                alarmCount = 1
            }
            3 -> {
                timeHours3LiveData.postValue("09")
                timeMinutes3LiveData.postValue("00")
                alarmTime3Visible.postValue(View.GONE)
                deleteTime2Visible.postValue(View.VISIBLE)
                alarmCount = 2
            }
        }
    }

}