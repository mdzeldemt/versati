package com.liuvil.versati.preferences.db.connection

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.liuvil.versati.framework.database.EntityConstants.KEY_AUTO_GENERATED
import java.net.URL

@Entity(tableName = "connections")
class Connection(
    @PrimaryKey(autoGenerate = true)
    val id: Long = KEY_AUTO_GENERATED,

    val name: String,
    val baseURL: URL
)
