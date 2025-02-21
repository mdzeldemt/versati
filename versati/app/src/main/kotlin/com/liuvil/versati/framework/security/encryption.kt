package com.liuvil.versati.framework.security

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class EncryptionResult(
    val value: ByteArray,
    val iv: ByteArray
)

fun encrypt(value: ByteArray, algorithm: String, key: Key): EncryptionResult {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return EncryptionResult(
        iv = cipher.iv,
        value = cipher.doFinal(value),
    )
}

fun decrypt(value: ByteArray, iv: ByteArray, algorithm: String, key: Key): ByteArray {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
    return cipher.doFinal(value)
}
