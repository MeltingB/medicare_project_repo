package com.meltingb.medicare.db.repo

import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.db.dao.PillDao
import com.meltingb.medicare.utils.debug
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PillRepositoryImpl(private var pillDao: PillDao) : PillRepository {

    override fun insertPill(pillEntity: PillEntity): Completable {
        return pillDao.insertPill(pillEntity)
    }

    override fun getAlarmsByDate(date: String): Single<List<PillEntity>> {
        return pillDao.getAlarmsByDate(date)
    }

    override fun updateTakeListByID(takeList: List<String>, pillID: Int): Completable {
        return pillDao.updateTakeListByID(takeList, pillID)
    }

    override fun getPillInfoByID(pillID: Int): Single<PillEntity> {
        return pillDao.getPillInfoByID(pillID)
    }

    override fun getAllAlarm(runBlock: (List<PillEntity>) -> Unit): Disposable {
        return pillDao.getAllAlarm()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                runBlock(it)
            }, {
                debug("error ${it.message}")
            })
    }

    override fun deletePill(pillID: Int, runBlcok: (Boolean) -> Unit): Disposable {
        return pillDao.deletePill(pillID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                runBlcok(true)
            }, {
                runBlcok(false)
                debug("error ${it.message}")
            })
    }

}