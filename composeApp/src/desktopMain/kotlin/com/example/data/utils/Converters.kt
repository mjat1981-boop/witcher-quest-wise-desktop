package com.example.data.utils

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
    private val moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(type)

    @TypeConverter
    fun fromStringList(value: List<String>): String = adapter.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = try {
        adapter.fromJson(value) ?: emptyList()
    } catch (e: Exception) {
        // A corrupt/malformed DB value should degrade to an empty list, not crash.
        emptyList()
    }
}
