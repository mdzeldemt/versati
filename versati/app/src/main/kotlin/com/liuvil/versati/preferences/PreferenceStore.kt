package com.liuvil.versati.preferences

import com.liuvil.versati.preferences.data.Server
import com.liuvil.versati.preferences.data.egg.ServerEgg
import com.liuvil.versati.preferences.database.PreferenceDatabase
import com.liuvil.versati.preferences.database.data.conversion.toDatabase
import javax.inject.Inject

class PreferenceStore @Inject constructor(
    preferenceDatabase: PreferenceDatabase
) {
    private val serverDao = preferenceDatabase.serverDao()

    suspend fun getAllServers(): List<Server> =
        serverDao.getAllServers()

    suspend fun createServer(egg: ServerEgg) =
        serverDao.insertServer(egg.toDatabase())

    suspend fun deleteServer(id: Int) =
        serverDao.deleteServerById(id)
}