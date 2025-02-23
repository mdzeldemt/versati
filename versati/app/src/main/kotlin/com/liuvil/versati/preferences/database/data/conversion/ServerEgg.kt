package com.liuvil.versati.preferences.database.data.conversion

import com.liuvil.versati.framework.database.EntityConstants
import com.liuvil.versati.preferences.data.egg.ServerEgg
import com.liuvil.versati.preferences.database.data.Server

fun ServerEgg.toDatabase(): Server =
    Server(
        id = EntityConstants.KEY_AUTO_GENERATED,
        name = name,
        baseURL = baseURL,
        credential = credential.toDatabase()
    )