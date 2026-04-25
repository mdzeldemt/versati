package com.mdzeldemt.versati.preferences

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mdzeldemt.versati.framework.encryption.Secret
import com.mdzeldemt.versati.framework.encryption.decrypt
import com.mdzeldemt.versati.framework.encryption.encrypt
import com.mdzeldemt.versati.framework.flow.flatMap
import com.mdzeldemt.versati.framework.keystore.MissingSecretKeyException
import com.mdzeldemt.versati.framework.keystore.generateSecretKey
import com.mdzeldemt.versati.framework.keystore.loadSecretKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.net.URL
import javax.inject.Inject

private const val PREFERENCES_DATA_STORE_NAME = "preferences"

private const val DEFAULT_ENTRIES_PER_PAGE = 10
private val DEFAULT_COLOR_SCHEME = ColorScheme.SYSTEM

private val Context.dataStore by preferencesDataStore(name = PREFERENCES_DATA_STORE_NAME)

private object PreferenceKey {
    val BASE_URL = stringPreferencesKey("base_url")

    val CREDENTIAL_TYPE = stringPreferencesKey("credential_type")

    val CREDENTIAL_USERNAME_CIPHERTEXT = stringPreferencesKey("credential_username_ciphertext")
    val CREDENTIAL_USERNAME_IV = stringPreferencesKey("credential_username_iv")
    val CREDENTIAL_PASSWORD_CIPHERTEXT = stringPreferencesKey("credential_password_ciphertext")
    val CREDENTIAL_PASSWORD_IV = stringPreferencesKey("credential_password_iv")

    val CREDENTIAL_API_KEY_CIPHERTEXT = stringPreferencesKey("credential_api_key_ciphertext")
    val CREDENTIAL_API_KEY_IV = stringPreferencesKey("credential_api_key_iv")

    val ENTRIES_PER_PAGE = intPreferencesKey("entries_per_page")

    val COLOR_SCHEME = stringPreferencesKey("color_scheme")
}

private enum class CredentialType {
    API_KEY,
    BASIC
}

@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val baseUrl = getUrl(PreferenceKey.BASE_URL)

    suspend fun setBaseUrl(
        value: URL?
    ) {
        setUrl(PreferenceKey.BASE_URL, value)
    }

    private val credentialType: Flow<CredentialType?> =
        getString(PreferenceKey.CREDENTIAL_TYPE)
            .flatMap { CredentialType.valueOf(it) }

    private val apiKey: Flow<String?> =
        getSecret(
            PreferenceKey.CREDENTIAL_API_KEY_CIPHERTEXT,
            PreferenceKey.CREDENTIAL_API_KEY_IV
        ).flatMap { it.decodeToString() }

    private val username: Flow<String?> =
        getSecret(
            PreferenceKey.CREDENTIAL_USERNAME_CIPHERTEXT,
            PreferenceKey.CREDENTIAL_USERNAME_IV
        ).flatMap { it.decodeToString() }

    private val password: Flow<String?> =
        getSecret(
            PreferenceKey.CREDENTIAL_PASSWORD_CIPHERTEXT,
            PreferenceKey.CREDENTIAL_PASSWORD_IV
        ).flatMap { it.decodeToString() }

    val credentials: Flow<Credentials?> =
        credentialType.flatMapLatest { credentialType ->
            when (credentialType) {
                CredentialType.API_KEY ->
                    apiKey
                        .flatMap { apiKey ->
                            ApiKeyCredentials(
                                apiKey = apiKey
                            )
                        }

                CredentialType.BASIC ->
                    combine(username, password) { username, password ->
                        if (username != null && password != null) {
                            BasicCredentials(
                                username = username,
                                password = password
                            )
                        } else null
                    }

                null -> flowOf(null)
            }
        }

    suspend fun setCredentials(
        value: Credentials?
    ) {
        when (value) {
            is ApiKeyCredentials -> {
                setApiKey(value.apiKey)
                setUsername(null)
                setPassword(null)
                setCredentialType(CredentialType.API_KEY)
            }

            is BasicCredentials -> {
                setUsername(value.username)
                setPassword(value.password)
                setApiKey(null)
                setCredentialType(CredentialType.BASIC)
            }

            null -> {
                setCredentialType(null)
                setApiKey(null)
                setUsername(null)
                setPassword(null)
            }
        }
    }

    private suspend fun setCredentialType(
        value: CredentialType?
    ) {
        setString(PreferenceKey.CREDENTIAL_TYPE, value?.name)
    }

    private suspend fun setApiKey(
        value: String?
    ) {
        setSecret(
            PreferenceKey.CREDENTIAL_API_KEY_CIPHERTEXT,
            PreferenceKey.CREDENTIAL_API_KEY_IV,
            value?.encodeToByteArray()
        )
    }

    private suspend fun setUsername(
        value: String?
    ) {
        setSecret(
            PreferenceKey.CREDENTIAL_USERNAME_CIPHERTEXT,
            PreferenceKey.CREDENTIAL_USERNAME_IV,
            value?.encodeToByteArray()
        )
    }

    private suspend fun setPassword(
        value: String?
    ) {
        setSecret(
            PreferenceKey.CREDENTIAL_PASSWORD_CIPHERTEXT,
            PreferenceKey.CREDENTIAL_PASSWORD_IV,
            value?.encodeToByteArray()
        )
    }

    val entriesPerPage: Flow<Int> =
        getInt(PreferenceKey.ENTRIES_PER_PAGE)
            .map {
                it ?: DEFAULT_ENTRIES_PER_PAGE
            }

    suspend fun setEntriesPerPage(
        value: Int
    ) {
        setInt(PreferenceKey.ENTRIES_PER_PAGE, value)
    }

    val colorScheme: Flow<ColorScheme> =
        getString(PreferenceKey.COLOR_SCHEME)
            .map {
                if (it != null) {
                    ColorScheme.valueOf(it)
                } else {
                    DEFAULT_COLOR_SCHEME
                }
            }

    suspend fun setColorScheme(
        value: ColorScheme
    ) {
        setString(PreferenceKey.COLOR_SCHEME, value.name)
    }

    private fun getSecret(
        ciphertextKey: Preferences.Key<String>,
        ivKey: Preferences.Key<String>
    ): Flow<ByteArray?> =
        combine(
            getByteArray(ciphertextKey),
            getByteArray(ivKey)
        ) { ciphertext, iv ->
            if (ciphertext != null && iv != null) {
                decrypt(
                    secret = Secret(ciphertext, iv),
                    key = loadSecretKey() ?: throw MissingSecretKeyException()
                )
            } else null
        }

    private suspend fun setSecret(
        ciphertextKey: Preferences.Key<String>,
        ivKey: Preferences.Key<String>,
        value: ByteArray?
    ) {
        if (value == null) {
            setByteArray(ciphertextKey, null)
            setByteArray(ivKey, null)
            return
        }

        val secret = encrypt(
            value = value,
            key = loadSecretKey() ?: generateSecretKey()
        )
        setByteArray(ciphertextKey, secret.ciphertext)
        setByteArray(ivKey, secret.iv)
    }

    private fun getByteArray(
        key: Preferences.Key<String>
    ): Flow<ByteArray?> =
        getString(key)
            .flatMap { Base64.decode(it, Base64.NO_WRAP) }

    private fun getUrl(
        key: Preferences.Key<String>
    ): Flow<URL?> =
        getString(key)
            .flatMap { URL(it) }

    private fun getString(
        key: Preferences.Key<String>
    ): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[key]
        }

    private fun getInt(
        key: Preferences.Key<Int>
    ): Flow<Int?> =
        context.dataStore.data.map { preferences ->
            preferences[key]
        }

    private suspend fun setByteArray(
        key: Preferences.Key<String>,
        value: ByteArray?
    ) {
        setString(
            key,
            value?.let {
                Base64.encodeToString(it, Base64.NO_WRAP)
            }
        )
    }

    private suspend fun setUrl(
        key: Preferences.Key<String>,
        value: URL?
    ) {
        setString(key, value?.toString())
    }

    private suspend fun setString(
        key: Preferences.Key<String>,
        value: String?
    ) {
        context.dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(key)
            } else {
                preferences[key] = value
            }
        }
    }

    private suspend fun setInt(
        key: Preferences.Key<Int>,
        value: Int?
    ) {
        context.dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(key)
            } else {
                preferences[key] = value
            }
        }
    }
}