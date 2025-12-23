package com.liuvil.versati.preferences

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PREFERENCES_DATA_STORE_NAME = "preferences"

private val Context.dataStore by preferencesDataStore(name = PREFERENCES_DATA_STORE_NAME)

private object PreferenceKey {
    val ACTIVE_CONNECTION_ID = stringPreferencesKey("active_connection_id")
}

class PreferenceStore @Inject constructor(
    @ApplicationContext val context: Context
) {
    suspend fun getActiveConnectionID(): Long? =
        context.dataStore.data
            .map { preferences ->
                preferences[PreferenceKey.ACTIVE_CONNECTION_ID]?.toLong()
            }
            .first()
}