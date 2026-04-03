package com.liuvil.versati.framework.encryption

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

const val ENCRYPTION_ALGORITHM_DEFAULT = "AES/GCM/NoPadding"

class Secret(
    val ciphertext: ByteArray,
    val iv: ByteArray
)

fun encrypt(
    value: ByteArray,
    algorithm: String = ENCRYPTION_ALGORITHM_DEFAULT,
    key: Key
): Secret {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return Secret(
        ciphertext = cipher.doFinal(value),
        iv = cipher.iv,
    )
}

fun decrypt(
    secret: Secret,
    algorithm: String = ENCRYPTION_ALGORITHM_DEFAULT,
    key: Key
): ByteArray {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, secret.iv))
    return cipher.doFinal(secret.ciphertext)
}
