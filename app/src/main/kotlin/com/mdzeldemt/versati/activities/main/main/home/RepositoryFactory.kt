package com.mdzeldemt.versati.activities.main.main.home

import com.mdzeldemt.versati.preferences.PreferenceStore
import com.mdzeldemt.versati.repository.Repository
import com.mdzeldemt.versati.repository.api.ApiClientFactory
import com.mdzeldemt.versati.repository.cache.CacheDatabase
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