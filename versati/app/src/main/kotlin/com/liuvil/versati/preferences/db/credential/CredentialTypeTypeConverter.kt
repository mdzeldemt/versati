package com.liuvil.versati.preferences.db.credential

import androidx.room.TypeConverter

class CredentialTypeTypeConverter {
    @TypeConverter
    fun toString(value: CredentialType) = value.name

    @TypeConverter
    fun fromString(value: String) = CredentialType.valueOf(value)
}