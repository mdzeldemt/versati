package com.liuvil.versati.activities.main.home

import com.liuvil.versati.preferences.CredentialRepository
import com.liuvil.versati.preferences.ConnectionRepository
import com.liuvil.versati.repository.Repository
import com.liuvil.versati.repository.api.APIClientFactory
import com.liuvil.versati.repository.cache.CacheDatabase
import javax.inject.Inject

class RepositoryFactory @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val credentialRepository: CredentialRepository,
    private val apiClientFactory: APIClientFactory,
    private val cacheDatabase: CacheDatabase
) {
    suspend fun create(
        connectionID: Long
    ): Repository {
        val connection = connectionRepository.getByID(connectionID)
        val credentials = credentialRepository.getByConnectionID(connectionID)
        val apiClient = apiClientFactory.create(connection.baseURL, credentials)
        return Repository(apiClient, cacheDatabase)
    }
}