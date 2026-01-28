package com.example.ehefin_mobile.core.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room TypeConverters for complex types
 */
class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }
    
    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
}