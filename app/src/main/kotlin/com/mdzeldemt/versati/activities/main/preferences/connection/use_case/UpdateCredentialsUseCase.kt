package com.mdzeldemt.versati.activities.main.preferences.connection.use_case

import com.mdzeldemt.versati.preferences.Credentials
import com.mdzeldemt.versati.preferences.PreferenceStore
import javax.inject.Inject

internal class UpdateCredentialsUseCase @Inject constructor(
    private val preferencesStore: PreferenceStore
) {
    suspend fun perform(
        value: Credentials?
    ): Result<Unit> {
        return runCatching {
            preferencesStore.setCredentials(value)
        }
    }
}