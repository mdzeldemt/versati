package com.mdzeldemt.versati.activities.main.preferences.connection.use_case

import com.mdzeldemt.versati.preferences.PreferenceStore
import java.net.URL
import javax.inject.Inject

internal class UpdateBaseUrlUseCase @Inject constructor(
    private val preferencesStore: PreferenceStore
) {
    suspend fun perform(
        value: URL?
    ): Result<Unit> {
        return runCatching {
            preferencesStore.setBaseUrl(value)
        }
    }
}