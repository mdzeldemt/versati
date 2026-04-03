package com.liuvil.versati.activities.main.main.home

import com.liuvil.versati.preferences.PreferenceStore
import com.liuvil.versati.repository.Repository
import com.liuvil.versati.repository.api.ApiClientFactory
import com.liuvil.versati.repository.cache.CacheDatabase
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RepositoryFactory @Inject constructor(
    private val apiClientFactory: ApiClientFactory,
    private val cacheDatabase: CacheDatabase,
    private val preferenceStore: PreferenceStore
) {
    suspend fun create(): Repository {
        val baseUrl = preferenceStore.baseUrl.filterNotNull().first()
        val credentials = preferenceStore.credentials.filterNotNull().first()
        val apiClient = apiClientFactory.create(baseUrl, credentials)
        return Repository(apiClient, cacheDatabase)
    }
}