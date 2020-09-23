package com.lukaszgalinski.gamefuture.repositories

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class StringTypeConverter {

    @TypeConverter
    fun stringToList(data: String): ArrayList<String>{
        val listType = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<String>): String? {
        val gson = Gson()
        val json = gson.toJson(list)
        return json
    }
}