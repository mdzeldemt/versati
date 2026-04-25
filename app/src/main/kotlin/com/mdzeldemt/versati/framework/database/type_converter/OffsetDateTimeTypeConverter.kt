package com.mdzeldemt.versati.framework.database.type_converter

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeTypeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromOffsetDateTime(value: OffsetDateTime?): String? =
        value?.format(formatter)

    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? =
        value?.let { OffsetDateTime.parse(it, formatter) }
}