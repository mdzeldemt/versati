package com.liuvil.versati.framework.database.type_converter

import androidx.room.TypeConverter
import java.net.URL

class URLTypeConverter {
    @TypeConverter
    fun fromURL(value: URL?): String? =
        value?.toString()

    @TypeConverter
    fun toURL(value: String?): URL? =
        value?.let { URL(it) }
}