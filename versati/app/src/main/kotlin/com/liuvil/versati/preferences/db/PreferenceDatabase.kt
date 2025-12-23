package com.liuvil.versati.preferences.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.liuvil.versati.framework.database.type_converter.URLTypeConverter
import com.liuvil.versati.preferences.db.credential.Credentials
import com.liuvil.versati.preferences.db.credential.CredentialDAO
import com.liuvil.versati.preferences.db.connection.Connection
import com.liuvil.versati.preferences.db.connection.ConnectionDAO

@Database(
    entities = [
        Connection::class,
        Credentials::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    URLTypeConverter::class
)
abstract class PreferenceDatabase: RoomDatabase() {
    abstract fun connectionDAO(): ConnectionDAO
    abstract fun credentialDAO(): CredentialDAO
}