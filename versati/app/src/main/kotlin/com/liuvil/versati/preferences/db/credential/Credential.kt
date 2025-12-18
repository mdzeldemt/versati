package com.liuvil.versati.preferences.db.credential

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.liuvil.versati.preferences.db.server.Server

@Entity(
    tableName = "credentials",
    foreignKeys = [
        ForeignKey(
            entity = Server::class,
            parentColumns = ["id"],
            childColumns = ["serverID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["serverID"])
    ]
)
@TypeConverters(CredentialTypeTypeConverter::class)
class Credential(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val serverID: Int,
    val credentialType: CredentialType,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val ciphertext: ByteArray,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val iv: ByteArray
)
