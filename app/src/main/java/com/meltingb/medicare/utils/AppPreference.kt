package com.meltingb.medicare.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONException

object AppPreference {

    private lateinit var preferences: SharedPreferences
    private val gson: Gson =
        GsonBuilder().registerTypeHierarchyAdapter(DateTime::class.java, DateTimeConverter())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()


    fun init(ctx: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
    }


    fun clear(key: String) {
        preferences.edit()
            .putString(key, null)
            .apply()
    }


    fun set(key: String, value: Any) {
        preferences.edit().apply {
            when (value) {
                is String -> this.putString(key, value)
                is Int -> this.putInt(key, value)
                is Boolean -> this.putBoolean(key, value)
                is Float -> this.putFloat(key, value)
                is Long -> this.putLong(key, value)

                else -> throw Exception("is not define type cast")
            }
        }.apply()
    }


    fun <T> get(key: String, defValue: T): T {
        @Suppress("UNCHECKED_CAST")
        return when (defValue) {
            is String -> preferences.getString(key, defValue) as T
            is Int -> preferences.getInt(key, defValue) as T
            is Boolean -> preferences.getBoolean(key, defValue) as T
            is Float -> preferences.getFloat(key, defValue) as T
            is Long -> preferences.getLong(key, defValue) as T

            else -> throw Exception("is not define type cast")
        }
    }

    fun <T> setObject(key: String, model: T) {
        val json = gson.toJson(model)
        preferences.edit().putString(key, json).apply()
    }

    fun <T> getObject(key: String, model: Class<T>): T? {
        val json: String? = preferences.getString(key, null)
        if (json != null) {
            return gson.fromJson(json, model)
        } else {
            return null
        }
    }

    fun <T> setObjectList(key: String, values: MutableList<T>) {
        val jsonArray = JSONArray()

        for (i in values.indices) {
            val value = gson.toJson(values[i])
            jsonArray.put(value)
        }

        if (values.isNotEmpty()) {
            preferences.edit().putString(key, jsonArray.toString()).apply()
        } else {
            preferences.edit().putString(key, null).apply()
        }
    }


    fun <T> getObjectList(key: String, model: Class<T>): MutableList<T>? {
        val json: String? = preferences.getString(key, null)
        val list = mutableListOf<T>()
        if (json != null) {
            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val data = gson.fromJson(jsonArray.getString(i), model)
                    list.add(data)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            return null
        }
        return list
    }


}