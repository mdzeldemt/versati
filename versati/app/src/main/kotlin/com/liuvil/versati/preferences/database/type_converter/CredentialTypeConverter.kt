package com.liuvil.versati.preferences.database.type_converter

import androidx.room.TypeConverter
import com.liuvil.versati.framework.json.buildPolymorphicJson
import com.liuvil.versati.preferences.database.data.Credential
import com.liuvil.versati.preferences.database.data.MinifluxAPIKeyCredential
import com.liuvil.versati.preferences.database.data.MinifluxBasicCredential
import kotlinx.serialization.modules.subclass

class CredentialTypeConverter {
    private val json = buildPolymorphicJson<Credential> {
        subclass(MinifluxBasicCredential::class)
        subclass(MinifluxAPIKeyCredential::class)
    }

    @TypeConverter
    fun fromCredential(value: Credential): String =
        json.encodeToString(value)

    @TypeConverter
    fun toCredential(value: String): Credential =
        json.decodeFromString(value)
}