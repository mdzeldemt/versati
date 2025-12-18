package com.liuvil.versati.preferences

import com.liuvil.versati.framework.database.EntityConstants.KEY_AUTO_GENERATED
import com.liuvil.versati.framework.encryption.Secret
import com.liuvil.versati.framework.encryption.decrypt
import com.liuvil.versati.framework.encryption.encrypt
import com.liuvil.versati.framework.keystore.MissingSecretKeyException
import com.liuvil.versati.framework.keystore.generateSecretKey
import com.liuvil.versati.framework.keystore.loadSecretKey
import com.liuvil.versati.preferences.data.APIKeyCredential
import com.liuvil.versati.preferences.data.BasicCredential
import com.liuvil.versati.preferences.data.Credential
import com.liuvil.versati.preferences.db.credential.CredentialDAO
import com.liuvil.versati.preferences.db.credential.CredentialType
import com.liuvil.versati.preferences.serialization.deserialize
import com.liuvil.versati.preferences.serialization.serialize
import javax.inject.Inject

class CredentialRepository @Inject constructor(
    private val credentialDAO: CredentialDAO
) {
    suspend fun getByServerID(
        serverID: Int
    ): Credential? {
        val credential = credentialDAO.getByServerID(serverID) ?: return null
        val secret = Secret(credential.ciphertext, credential.iv)

        val payload = decrypt(
            secret = secret,
            key = loadSecretKey() ?: throw MissingSecretKeyException()
        )

        return deserialize(payload, credential.credentialType)
    }

    suspend fun upsert(
        serverID: Int,
        credential: Credential,
    ) {
        val payload = serialize(credential)

        val secret = encrypt(
            value = payload,
            key = loadSecretKey() ?: generateSecretKey()
        )

        credentialDAO.upsert(
            com.liuvil.versati.preferences.db.credential.Credential(
                id = KEY_AUTO_GENERATED,
                serverID = serverID,
                credentialType = when (credential) {
                    is BasicCredential -> CredentialType.BASIC
                    is APIKeyCredential -> CredentialType.API_KEY
                },
                ciphertext = secret.ciphertext,
                iv = secret.iv
            )
        )
    }
}