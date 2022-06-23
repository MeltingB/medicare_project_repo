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

class HomeViewModel(
    var pillRepo: PillRepository
) : BaseViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    val navigationLiveData: SingleLiveEvent<NavigationEvent> get() = _navigationLiveData
    private val _navigationLiveData = SingleLiveEvent<NavigationEvent>()

    val alarmListLiveData = MutableLiveData<MutableList<PillEntity>>()

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
    fun getAlarmList(date: String, day: Int) {
        pillRepo.getAlarmsByDate(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val alarmList = mutableListOf<PillEntity>()
                val dayIndex = if (day == 7) 0 else day
                if (it.isNotEmpty()) {
                    it.forEach { data ->
                        if (data.alarmWeek[dayIndex]) {
                            alarmList.add(data)
                        }
                    }
                    alarmListLiveData.postValue(alarmList)
                } else {
                    alarmListLiveData.postValue(alarmList)
                }
                AppPreference.setObjectList<PillEntity>(PREF_KEY_PILL_LIST, alarmList)
            }, {
                Log.w("error", "${it.message}")
            })
    }

    @SuppressLint("CheckResult")
    fun updateTakeList(takeList: List<String>, id: Int, ) {

        pillRepo.updateTakeListByID(takeList, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                Log.w("error", "${it.message}")
            })
    }

}