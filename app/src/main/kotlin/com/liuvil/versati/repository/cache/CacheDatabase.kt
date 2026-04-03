package com.liuvil.versati.repository.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.liuvil.versati.framework.database.type_converter.OffsetDateTimeTypeConverter
import com.liuvil.versati.framework.database.type_converter.UrlTypeConverter
import com.liuvil.versati.repository.cache.dao.CategoryCache
import com.liuvil.versati.repository.cache.dao.EnclosureCache
import com.liuvil.versati.repository.cache.dao.EntryCache
import com.liuvil.versati.repository.cache.dao.FeedCache
import com.liuvil.versati.repository.cache.dao.IconCache
import com.liuvil.versati.repository.cache.data.Category
import com.liuvil.versati.repository.cache.data.Enclosure
import com.liuvil.versati.repository.cache.data.Entry
import com.liuvil.versati.repository.cache.data.Feed
import com.liuvil.versati.repository.cache.data.Icon

@Database(
    entities = [
        Category::class,
        Enclosure::class,
        Entry::class,
        Feed::class,
        Icon::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(OffsetDateTimeTypeConverter::class, UrlTypeConverter::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun categoryCache(): CategoryCache
    abstract fun enclosureCache(): EnclosureCache
    abstract fun entryCache(): EntryCache
    abstract fun feedCache(): FeedCache
    abstract fun iconCache(): IconCache
}
