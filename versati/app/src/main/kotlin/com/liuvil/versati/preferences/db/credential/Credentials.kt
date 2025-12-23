package com.liuvil.versati.preferences.db.credential

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.liuvil.versati.framework.database.EntityConstants.KEY_AUTO_GENERATED
import com.liuvil.versati.preferences.db.connection.Connection

@Entity(
    tableName = "credentials",
    foreignKeys = [
        ForeignKey(
            entity = Connection::class,
            parentColumns = ["id"],
            childColumns = ["connectionID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["connectionID"])
    ]
)
@TypeConverters(CredentialTypeTypeConverter::class)
class Credentials(
    @PrimaryKey(autoGenerate = true)
    val id: Long = KEY_AUTO_GENERATED,

    val connectionID: Long,
    val credentialType: CredentialType,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val ciphertext: ByteArray,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val iv: ByteArray
)
