package com.mdzeldemt.versati.framework.database.type_converter

import androidx.room.TypeConverter
import java.net.URL

class UrlTypeConverter {
    @TypeConverter
    fun fromUrl(value: URL?): String? =
        value?.toString()

    @TypeConverter
    fun toUrl(value: String?): URL? =
        value?.let { URL(it) }
}