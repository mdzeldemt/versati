package com.liuvil.versati.activities.main.home

import com.liuvil.versati.preferences.CredentialRepository
import com.liuvil.versati.preferences.ServerRepository
import com.liuvil.versati.repository.Repository
import com.liuvil.versati.repository.api.APIClientFactory
import com.liuvil.versati.repository.cache.CacheDatabase
import javax.inject.Inject

class RepositoryFactory @Inject constructor(
    private val serverRepository: ServerRepository,
    private val credentialRepository: CredentialRepository,
    private val apiClientFactory: APIClientFactory,
    private val cacheDatabase: CacheDatabase
) {
    suspend fun create(
        serverID: Int
    ): Repository {
        val server = serverRepository.getByID(serverID) ?: throw RuntimeException()
        val credential = credentialRepository.getByServerID(serverID) ?: throw RuntimeException()
        val apiClient = apiClientFactory.create(server.baseURL, credential)
        return Repository(apiClient, cacheDatabase)
    }
}