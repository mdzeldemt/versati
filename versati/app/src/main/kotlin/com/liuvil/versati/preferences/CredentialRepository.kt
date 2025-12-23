package com.liuvil.versati.preferences

import com.liuvil.versati.framework.encryption.Secret
import com.liuvil.versati.framework.encryption.decrypt
import com.liuvil.versati.framework.encryption.encrypt
import com.liuvil.versati.framework.keystore.MissingSecretKeyException
import com.liuvil.versati.framework.keystore.generateSecretKey
import com.liuvil.versati.framework.keystore.loadSecretKey
import com.liuvil.versati.preferences.data.APIKeyCredentials
import com.liuvil.versati.preferences.data.BasicCredentials
import com.liuvil.versati.preferences.data.Credentials
import com.liuvil.versati.preferences.db.credential.CredentialDAO
import com.liuvil.versati.preferences.db.credential.CredentialType
import com.liuvil.versati.preferences.serialization.deserialize
import com.liuvil.versati.preferences.serialization.serialize
import javax.inject.Inject

class CredentialRepository @Inject constructor(
    private val credentialDAO: CredentialDAO
) {

    suspend fun getByConnectionID(
        connectionID: Long
    ): Credentials {
        val credential = credentialDAO.getByConnectionID(connectionID)
        val secret = Secret(credential.ciphertext, credential.iv)

        val payload = decrypt(
            secret = secret,
            key = loadSecretKey() ?: throw MissingSecretKeyException()
        )

        return deserialize(payload, credential.credentialType)
    }

    suspend fun upsert(
        connectionID: Long,
        credentials: Credentials,
    ) {
        val payload = serialize(credentials)

        val secret = encrypt(
            value = payload,
            key = loadSecretKey() ?: generateSecretKey()
        )

        credentialDAO.upsert(
            com.liuvil.versati.preferences.db.credential.Credentials(
                connectionID = connectionID,
                credentialType = when (credentials) {
                    is BasicCredentials -> CredentialType.BASIC
                    is APIKeyCredentials -> CredentialType.API_KEY
                },
                ciphertext = secret.ciphertext,
                iv = secret.iv
            )
        )
    }
}