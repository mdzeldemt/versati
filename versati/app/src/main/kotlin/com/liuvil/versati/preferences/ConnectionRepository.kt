package com.liuvil.versati.preferences

import com.liuvil.versati.preferences.data.Connection
import com.liuvil.versati.preferences.db.connection.ConnectionDAO
import java.net.URL
import javax.inject.Inject

class ConnectionRepository @Inject constructor(
    private val connectionDAO: ConnectionDAO
) {
    suspend fun getAll(): List<Connection> =
        connectionDAO.getAll()
            .map {
                Connection(
                    id = it.id,
                    name = it.name,
                    baseURL = it.baseURL
                )
            }

    suspend fun getByID(
        id: Long
    ): Connection = connectionDAO.getById(id)
        .let {
            Connection(
                id = it.id,
                name = it.name,
                baseURL = it.baseURL
            )
        }

    suspend fun create(
        name: String,
        baseURL: URL
    ): Connection {
        val connectionID = connectionDAO.upsert(
            com.liuvil.versati.preferences.db.connection.Connection(
                name = name,
                baseURL = baseURL
            )
        )
        return connectionDAO.getById(connectionID)
            .let {
                Connection(
                    id = it.id,
                    name = it.name,
                    baseURL = it.baseURL
                )
            }
    }

    suspend fun update(
        id: Long,
        name: String,
        baseURL: URL
    ): Connection {
        connectionDAO.upsert(
            com.liuvil.versati.preferences.db.connection.Connection(
                id = id,
                name = name,
                baseURL = baseURL
            )
        )
        return connectionDAO.getById(id)
            .let {
                Connection(
                    id = it.id,
                    name = it.name,
                    baseURL = it.baseURL
                )
            }
    }

    suspend fun delete(
        id: Long
    ) {
        connectionDAO.delete(id)
    }
}