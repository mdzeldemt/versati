package com.liuvil.versati.framework.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val ANDROID_KEYSTORE_PROVIDER_NAME = "AndroidKeyStore"
private const val SECRET_KEY_SIZE = 256

fun getSecretKey(alias: String): SecretKey? =
    KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER_NAME)
        .apply { load(null) }
        .getKey(alias, null) as? SecretKey

fun generateSecretKey(alias: String): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE_PROVIDER_NAME)
    keyGenerator.init(
        KeyGenParameterSpec.Builder(alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(SECRET_KEY_SIZE)
            .build()
    )
    return keyGenerator.generateKey()
}