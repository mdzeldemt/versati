package com.liuvil.versati.preferences

import com.liuvil.versati.preferences.data.Server
import com.liuvil.versati.preferences.db.server.ServerDAO
import javax.inject.Inject

class ServerRepository @Inject constructor(
    private val serverDAO: ServerDAO
) {
    suspend fun getAll(): List<Server> =
        serverDAO.getAll()
            .map {
                Server(
                    id = it.id,
                    name = it.name,
                    baseURL = it.baseURL
                )
            }

    suspend fun getByID(
        id: Int
    ): Server? = serverDAO.getById(id)
        ?.let {
            Server(
                id = it.id,
                name = it.name,
                baseURL = it.baseURL
            )
        }
}