package com.meltingb.medicare.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Entity(
    tableName = "pill_table"
)
@Parcelize
data class PillEntity(
    @PrimaryKey(autoGenerate = true)
    val pillID: Int?,
    val pillGroupID: String,
    val pillName: String,
    val takeNum: String,
    val takeType: String,
    val alarmWeek: List<Boolean>,
    val alarmTime: String,
    val pillImageNum: Int,
    val createAt: String,
    var takeDayList: List<String>,
    var alarmId: Int
): Parcelable
