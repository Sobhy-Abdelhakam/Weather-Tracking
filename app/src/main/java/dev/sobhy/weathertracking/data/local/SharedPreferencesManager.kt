package dev.sobhy.weathertracking.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import dev.sobhy.weathertracking.helper.Constant.PREF_NAME

object SharedPreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    }
    fun saveString(key: String, value: String){
        sharedPreferences.edit { putString(key, value) }
    }
    fun getString(key: String, defaultValue: String?): String?{
        return sharedPreferences.getString(key, defaultValue)
    }
}