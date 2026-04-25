package com.mdzeldemt.versati.activities.main.preferences.connection.use_case

import com.mdzeldemt.versati.activities.main.preferences.connection.Preferences
import com.mdzeldemt.versati.preferences.PreferenceStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class GetPreferencesUseCase @Inject constructor(
    private val preferencesStore: PreferenceStore
) {
    suspend fun perform(): Result<Preferences> {
        return runCatching {
            val baseUrl = preferencesStore.baseUrl.first()
            val credentials = preferencesStore.credentials.first()

            Preferences(
                baseUrl = baseUrl,
                credentials = credentials
            )
        }
    }
}