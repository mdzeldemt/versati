package com.liuvil.versati.activities.main.preferences.connection.use_case

import com.liuvil.versati.activities.main.preferences.connection.Preferences
import com.liuvil.versati.preferences.PreferenceStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class GetPreferencesUseCase @Inject constructor(
    private val preferencesStore: PreferenceStore
) {
    suspend operator fun invoke(): Result<Preferences> {
        return runCatching {
            val baseURL = preferencesStore.baseURL.first()
            val credentials = preferencesStore.credentials.first()

            Preferences(
                baseURL = baseURL,
                credentials = credentials
            )
        }
    }
}