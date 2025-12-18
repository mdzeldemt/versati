package com.liuvil.versati.preferences.db.server

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.liuvil.versati.preferences.db.credential.CredentialType
import java.net.URL
import java.util.UUID

@Entity(tableName = "servers")
class Server(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val name: String,
    val baseURL: URL,
    val credentialID: UUID,
    val credentialType: CredentialType
)
