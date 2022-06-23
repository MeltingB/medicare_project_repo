package com.meltingb.medicare.view.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.meltingb.medicare.core.BaseViewModel
import com.meltingb.medicare.core.SingleLiveEvent
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.db.repo.PillRepository
import com.meltingb.medicare.utils.*
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class TakeDetailViewModel(
    var pillRepo: PillRepository
) : BaseViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    val navigationLiveData: SingleLiveEvent<NavigationEvent> get() = _navigationLiveData
    private val _navigationLiveData = SingleLiveEvent<NavigationEvent>()

    val alarmListLiveData = MutableLiveData<MutableList<PillEntity>>()
    val detailListLiveData = MutableLiveData<MutableList<PillEntity>>()

    val pillInfoLiveData = MutableLiveData<PillEntity>()
    val pillInfoTextLiveData = MutableLiveData("")
    val pillAlarmTextLiveData = MutableLiveData("")
    val pillStartDtTextLiveData = MutableLiveData("")
    val takeDaysLiveData = MutableLiveData<List<String>>()

    fun moveMenu(menu: Int) {
        when (menu) {
            MENU_HOME -> _navigationLiveData.postValue(NavigationEvent.HomeView)
            MENU_HELP -> _navigationLiveData.postValue(NavigationEvent.HelpView)
            MENU_SEARCH -> _navigationLiveData.postValue(NavigationEvent.SearchView)
            MENU_MAP -> _navigationLiveData.postValue(NavigationEvent.MapView)
            MENU_ADD -> _navigationLiveData.postValue(NavigationEvent.AddView)
        }
    }

    @SuppressLint("CheckResult")
    fun getPillInfo(id: Int) {
        pillRepo.getPillInfoByID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                pillInfoLiveData.postValue(it)
                takeDaysLiveData.postValue(it.takeDayList)
            }, {
                debug("error ${it.message}")
            })
    }

    fun getAllPill() {
        pillRepo.getAllAlarm {
            alarmListLiveData.postValue(it.toMutableList())
        }
    }

    @SuppressLint("CheckResult")
    fun getAlarmList(date: String) {
        pillRepo.getAlarmsByDate(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                detailListLiveData.postValue(it.toMutableList())
                debug("list >> $it")
            }, {
                Log.w("error", "${it.message}")
            })
    }

}