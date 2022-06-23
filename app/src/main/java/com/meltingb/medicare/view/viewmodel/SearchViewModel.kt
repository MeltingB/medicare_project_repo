package com.meltingb.medicare.view.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.meltingb.medicare.api.Item
import com.meltingb.medicare.api.PillInfo
import com.meltingb.medicare.api.repo.ApiRepository
import com.meltingb.medicare.core.BaseViewModel
import com.meltingb.medicare.core.SingleLiveEvent
import com.meltingb.medicare.data.Details
import com.meltingb.medicare.data.PillDetails
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.db.repo.PillRepository
import com.meltingb.medicare.utils.*
import com.meltingb.medicare.view.fragment.SearchFragmentDirections
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class SearchViewModel(
    var apiRepo: ApiRepository
) : BaseViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    val navigationLiveData: SingleLiveEvent<NavigationEvent> get() = _navigationLiveData
    private val _navigationLiveData = SingleLiveEvent<NavigationEvent>()

    val searchTypeLiveData = MutableLiveData<Int>()
    val searchType: Int get() = searchTypeLiveData.value ?: 0
    val searchTextLiveData = MutableLiveData<String>()
    val searchText: String get() = searchTextLiveData.value ?: ""
    val searchListLiveData = MutableLiveData<List<Item>>()
    val hintTextLiveData = MutableLiveData<String>()
    val progressVisible = MutableLiveData(View.GONE)

    // 디테일 화면
    val pillDetailsLiveData = MutableLiveData<List<Details>>()
    val pillNameLiveData = MutableLiveData<String>()
    val pillSeqNumLiveData = MutableLiveData<String>()
    val pillEnterpriseLiveData = MutableLiveData<String>()
    val pillEdiCodeLiveData = MutableLiveData<String>()
    val pillShapeLiveData = MutableLiveData<String>()
    val pillColorLiveData = MutableLiveData<String>()
    val pillEfcyLiveData = MutableLiveData<String>()
    val pillUseLiveData = MutableLiveData<String>()
    val pillWarnLiveData = MutableLiveData<String>()
    val pillSideEffectLiveData = MutableLiveData<String>()
    val pillDepositLiveData = MutableLiveData<String>()

    fun moveMenu(menu: Int) {
        when (menu) {
            MENU_HOME -> _navigationLiveData.postValue(NavigationEvent.HomeView)
            MENU_HELP -> _navigationLiveData.postValue(NavigationEvent.HelpView)
            MENU_MAP -> _navigationLiveData.postValue(NavigationEvent.MapView)
            MENU_ADD -> _navigationLiveData.postValue(NavigationEvent.AddView)
        }
    }


    fun searchPillInfo(view: View) {
        if (searchText != "") {
            when (searchType) {
                TYPE_NAME -> searchByName(view.context)
                TYPE_SEQ -> searchBySeq(view.context)
                TYPE_CODE -> searchByCode(view.context)
            }
        }
    }

    @SuppressLint("CheckResult")
    fun searchByName(context: Context) {
        apiRepo.searchByName(searchText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressVisible.postValue(View.VISIBLE)
                _navigationLiveData.postValue(NavigationEvent.SearchView)
            }
            .subscribe({
                progressVisible.postValue(View.GONE)
                searchListLiveData.postValue(it.body.items.item)
            }, {
                Log.w("error>>>", "${it.message}")
                progressVisible.postValue(View.GONE)
                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            })
    }

    @SuppressLint("CheckResult")
    fun searchBySeq(context: Context) {
        apiRepo.searchBySeqNum(searchText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressVisible.postValue(View.VISIBLE)
                _navigationLiveData.postValue(NavigationEvent.SearchView)
            }
            .subscribe({
                progressVisible.postValue(View.GONE)
                searchListLiveData.postValue(it.body.items.item)
            }, {
                Log.w("error>>>", "${it.message}")
                progressVisible.postValue(View.GONE)
                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            })
    }

    @SuppressLint("CheckResult")
    fun searchByCode(context: Context) {
        apiRepo.searchByEdiCode(searchText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressVisible.postValue(View.VISIBLE)
                _navigationLiveData.postValue(NavigationEvent.SearchView)
            }
            .subscribe({
                progressVisible.postValue(View.GONE)
                searchListLiveData.postValue(it.body.items.item)
            }, {
                Log.w("error>>>", "${it.message}")
                progressVisible.postValue(View.GONE)
                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            })
    }

    @SuppressLint("CheckResult")
    fun getDetailsBySeqNum(seqNum: String, context: Context) {
        apiRepo.getDetailsBySeqNum(seqNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressVisible.postValue(View.VISIBLE)
                _navigationLiveData.postValue(NavigationEvent.SearchView)
            }
            .subscribe({
                progressVisible.postValue(View.GONE)
                pillDetailsLiveData.postValue(it.body.items.item)
            }, {
                Log.w("error>>>", "${it.message}")
                progressVisible.postValue(View.GONE)
                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            })
    }

    @SuppressLint("CheckResult")
    fun confirmDetails(item: Item, view: View) {
        apiRepo.getDetailsBySeqNum(item.itemSeq!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressVisible.postValue(View.VISIBLE)
                _navigationLiveData.postValue(NavigationEvent.SearchView)
            }
            .subscribe({
                progressVisible.postValue(View.GONE)
                moveDetailView(item, view)
            }, {
                Log.w("error>>>", "${it.message}")
                progressVisible.postValue(View.GONE)
                Toast.makeText(view.context, "상세 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            })
    }


    private fun moveDetailView(item: Item, view: View) {
        val direction = SearchFragmentDirections.actionSearchFragmentToSearchDetailFragment(item)
        view.findNavController().navigate(direction)
    }
}