package com.liuvil.versati.activities.main.preferences.connection.use_case

import com.liuvil.versati.activities.main.preferences.connection.Preferences
import com.liuvil.versati.preferences.Credentials
import com.liuvil.versati.preferences.PreferenceStore
import kotlinx.coroutines.flow.first
import java.net.URL
import javax.inject.Inject

internal class UpdateCredentialsUseCase @Inject constructor(
    private val preferencesStore: PreferenceStore
) {
    suspend operator fun invoke(
        value: Credentials?
    ): Result<Unit> {
        return runCatching {
            preferencesStore.setCredentials(value)
        }
    }
}