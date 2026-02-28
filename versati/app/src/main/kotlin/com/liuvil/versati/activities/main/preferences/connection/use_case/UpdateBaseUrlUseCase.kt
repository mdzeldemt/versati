package com.liuvil.versati.activities.main.preferences.connection.use_case

import com.liuvil.versati.preferences.PreferenceStore
import java.net.URL
import javax.inject.Inject

internal class UpdateBaseUrlUseCase @Inject constructor(
    private val preferencesStore: PreferenceStore
) {
    suspend operator fun invoke(
        value: URL?
    ): Result<Unit> {
        return runCatching {
            preferencesStore.setBaseURL(value)
        }
    }
}