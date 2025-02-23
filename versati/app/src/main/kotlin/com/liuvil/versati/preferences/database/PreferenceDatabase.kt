package com.liuvil.versati.preferences.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.liuvil.versati.framework.database.type_converter.URLTypeConverter
import com.liuvil.versati.preferences.database.dao.ServerDao
import com.liuvil.versati.preferences.database.data.Server

@Database(
    entities = [
        Server::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(URLTypeConverter::class)
abstract class PreferenceDatabase: RoomDatabase() {
    abstract fun serverDao(): ServerDao
}