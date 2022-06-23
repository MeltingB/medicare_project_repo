package com.meltingb.medicare.view.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.meltingb.medicare.api.repo.KakaoRepository
import com.meltingb.medicare.core.BaseViewModel
import com.meltingb.medicare.core.SingleLiveEvent
import com.meltingb.medicare.data.Document
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

class MapViewModel(
    var kakaoRepo: KakaoRepository
) : BaseViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    val navigationLiveData: SingleLiveEvent<NavigationEvent> get() = _navigationLiveData
    private val _navigationLiveData = SingleLiveEvent<NavigationEvent>()

    val hospitalListLiveData = MutableLiveData<List<Document>>()
    val pharmacyListLiveData = MutableLiveData<List<Document>>()
    val allListLiveData = MutableLiveData<List<Document>>()

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
    fun getHospital(x: String, y: String) {
        // 반경 1km
        kakaoRepo.searchByCategory(CATEGORY_CODE_HP, x, y, 1000)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.documents.isNotEmpty()) {
                    debug("${it.documents}")
                    hospitalListLiveData.postValue(it.documents)
                }
            }, {

            })
    }

    @SuppressLint("CheckResult")
    fun getPharmacy(x: String, y: String) {
        // 반경 1km
        kakaoRepo.searchByCategory(CATEGORY_CODE_PM, x, y, 1000)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.documents.isNotEmpty()) {
                    debug("${it.documents}")
                    pharmacyListLiveData.postValue(it.documents)
                }
            }, {

            })
    }

}