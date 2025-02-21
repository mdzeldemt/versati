package com.liuvil.versati.security

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import com.liuvil.versati.framework.security.decrypt
import com.liuvil.versati.framework.security.encrypt
import com.liuvil.versati.framework.security.generateSecretKey
import com.liuvil.versati.framework.security.getSecretKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.Key
import javax.inject.Inject

private const val SECRET_PREFERENCES_ALIAS = "secrets"
private const val SECRET_KEY_ALIAS = "secrets"
private const val ENCRYPTION_ALGORITHM_DEFAULT = "AES/GCM/NoPadding"
private const val PREFERENCE_KEY_SUFFIX_SECRET_VALUE = ".value"
private const val PREFERENCE_KEY_SUFFIX_SECRET_IV = ".iv"
private const val PREFERENCE_KEY_SUFFIX_SECRET_ALGORITHM = ".algorithm"

class SecretStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        SECRET_PREFERENCES_ALIAS,
        Context.MODE_PRIVATE
    )

    private val secretKey: Key
        get() = getSecretKey(SECRET_KEY_ALIAS)
            ?: generateSecretKey(SECRET_KEY_ALIAS)

    fun loadSecret(
        name: String
    ): String? {
        val value = sharedPreferences.getString(getValuePreferenceKey(name), null) ?: return null
        val iv = sharedPreferences.getString(getIVPreferenceKey(name), null) ?: return null
        val algorithm = sharedPreferences.getString(getAlgorithmPreferenceKey(name), null) ?: return null
        return decrypt(
            value = Base64.decode(value, Base64.DEFAULT),
            iv = Base64.decode(iv, Base64.DEFAULT),
            algorithm = algorithm,
            key = secretKey
        ).decodeToString()
    }

    fun storeSecret(
        name: String,
        secret: String
    ) {
        val encryptionResult = encrypt(
            value = secret.toByteArray(),
            algorithm = ENCRYPTION_ALGORITHM_DEFAULT,
            key = secretKey
        )
        sharedPreferences.edit {
            putString(getValuePreferenceKey(name), Base64.encodeToString(encryptionResult.value, Base64.DEFAULT))
            putString(getIVPreferenceKey(name), Base64.encodeToString(encryptionResult.iv, Base64.DEFAULT))
            putString(getAlgorithmPreferenceKey(name), ENCRYPTION_ALGORITHM_DEFAULT)
            apply()
        }
    }

    private fun getValuePreferenceKey(
        name: String
    ) = "$name$PREFERENCE_KEY_SUFFIX_SECRET_VALUE"

    private fun getIVPreferenceKey(
        name: String
    ) = "$name$PREFERENCE_KEY_SUFFIX_SECRET_IV"

    private fun getAlgorithmPreferenceKey(
        name: String
    ) = "$name$PREFERENCE_KEY_SUFFIX_SECRET_ALGORITHM"
}