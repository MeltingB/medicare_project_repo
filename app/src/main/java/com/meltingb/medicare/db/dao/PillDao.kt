package com.meltingb.medicare.db.dao

import androidx.room.*
import com.meltingb.medicare.data.PillEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPill(pillEntity: PillEntity): Completable

    @Query("SELECT * FROM pill_table")
    fun getAllAlarm(): Single<List<PillEntity>>

    @Query("SELECT * FROM pill_table WHERE Date(createAt) <= Date(:date)")
    fun getAlarmsByDate(date: String): Single<List<PillEntity>>

    @Query("UPDATE PILL_TABLE SET takeDayList = :takeList WHERE pillID = :pillID")
    fun updateTakeListByID(takeList: List<String>, pillID: Int): Completable

    @Query("SELECT * FROM PILL_TABLE WHERE pillID = :pillID")
    fun getPillInfoByID(pillID: Int): Single<PillEntity>

    @Query("DELETE FROM PILL_TABLE WHERE pillID = :pillID")
    fun deletePill(pillID: Int): Completable
}