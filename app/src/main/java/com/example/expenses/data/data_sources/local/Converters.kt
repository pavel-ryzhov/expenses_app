package com.example.expenses.data.data_sources.local

import androidx.room.TypeConverter
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun stringToMap(value: String): MutableMap<String, Double> {
        val json = JSONObject(value)
        val result = mutableMapOf<String, Double>()
        for (key in json.keys())
            result[key] = json.getDouble(key)
        return result
    }

    @TypeConverter
    fun mapToString(map: MutableMap<String, Double>): String {
        val json = JSONObject()
        for (entry in map.entries)
            json.put(entry.key, entry.value)
        return json.toString()
    }
}