package com.liuvil.versati.preferences.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.liuvil.versati.framework.database.type_converter.URLTypeConverter
import com.liuvil.versati.preferences.db.credential.Credential
import com.liuvil.versati.preferences.db.credential.CredentialDAO
import com.liuvil.versati.preferences.db.server.Server
import com.liuvil.versati.preferences.db.server.ServerDAO

@Database(
    entities = [
        Credential::class,
        Server::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    URLTypeConverter::class
)
abstract class PreferenceDatabase: RoomDatabase() {
    abstract fun credentialDAO(): CredentialDAO
    abstract fun serverDAO(): ServerDAO
}