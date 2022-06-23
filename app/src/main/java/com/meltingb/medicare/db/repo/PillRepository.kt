package com.meltingb.medicare.db.repo

import com.meltingb.medicare.data.PillEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

interface PillRepository {

    fun insertPill(pillEntity: PillEntity): Completable

    fun getAlarmsByDate(date: String): Single<List<PillEntity>>

    fun updateTakeListByID(takeList: List<String>, pillID: Int): Completable

    fun getPillInfoByID(pillID: Int): Single<PillEntity>

    fun getAllAlarm(runBlock: (List<PillEntity>) -> Unit): Disposable

    fun deletePill(pillID: Int, runBlcok:(Boolean)-> Unit) : Disposable
}