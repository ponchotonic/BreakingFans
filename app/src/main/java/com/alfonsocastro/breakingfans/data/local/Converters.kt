package com.alfonsocastro.breakingfans.data.local

import android.util.Log
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString()
    }

    @TypeConverter
    fun fromStringToStringList(string: String?): List<String>? {
        return string?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun fromIntList(list: List<Int>?): String? {
        return list?.joinToString()
    }

    @TypeConverter
    fun fromStringToIntList(string: String?): List<Int> {
        val stringList = fromStringToStringList(string)
        val intList = mutableListOf<Int>()
        stringList?.forEach {
            if (it.isNotEmpty()) {
                try {
                    val int = it.toInt()
                    intList.add(int)
                } catch (exception: NumberFormatException) {
                    Log.d("TypeConverter", "Error converting $it from $stringList", exception)
                }
            }
        }
        return intList
    }
}