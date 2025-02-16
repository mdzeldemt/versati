package com.liuvil.versati.repository.cache.database

import androidx.room.TypeConverter
import java.net.URL
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object RoomConverters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromOffsetDateTime(value: OffsetDateTime?): String? =
        value?.format(formatter)

    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? =
        value?.let { OffsetDateTime.parse(it, formatter) }

    @TypeConverter
    fun fromUrl(value: URL?): String? =
        value?.toString()

    @TypeConverter
    fun toUrl(value: String?): URL? =
        value?.let { URL(it) }
}