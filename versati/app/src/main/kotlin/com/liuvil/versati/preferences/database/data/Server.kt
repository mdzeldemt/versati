package com.liuvil.versati.preferences.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.liuvil.versati.preferences.data.Server
import com.liuvil.versati.preferences.database.type.CredentialTypeConverter
import java.net.URL

@Entity(tableName = "servers")
@TypeConverters(CredentialTypeConverter::class)
data class Server(
    @PrimaryKey(autoGenerate = true) override val id: Int,
    override val name: String,
    override val baseURL: URL,
    override val credential: Credential
): Server
