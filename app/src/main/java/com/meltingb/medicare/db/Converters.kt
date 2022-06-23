package com.meltingb.medicare.db

import androidx.room.TypeConverter
import com.google.gson.Gson


class Converters {
    @TypeConverter
    fun listToJson(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun listToJsonBool(value: List<Boolean>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToListBool(value: String) = Gson().fromJson(value, Array<Boolean>::class.java).toList()
}
