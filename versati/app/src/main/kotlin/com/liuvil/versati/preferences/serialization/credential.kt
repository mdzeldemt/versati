package com.liuvil.versati.preferences.serialization

import com.liuvil.versati.preferences.data.APIKeyCredential
import com.liuvil.versati.preferences.data.BasicCredential
import com.liuvil.versati.preferences.data.Credential
import com.liuvil.versati.preferences.db.credential.CredentialType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class BasicCredentialPayload(
    val username: String,
    val password: String
)

@Serializable
private data class APIKeyCredentialPayload(
    val apiKey: String
)

fun serialize(
    credential: Credential
): ByteArray =
    Json.encodeToString {
        when (credential) {
            is BasicCredential -> BasicCredentialPayload(credential.username, credential.password)
            is APIKeyCredential -> APIKeyCredentialPayload(credential.apiKey)
        }
    }.encodeToByteArray()

fun deserialize(
    value: ByteArray,
    credentialType: CredentialType
): Credential =
    when (credentialType) {
        CredentialType.BASIC -> {
            val payload = Json.decodeFromString<BasicCredentialPayload>(value.decodeToString())
            BasicCredential(payload.username, payload.password)
        }

        CredentialType.API_KEY -> {
            val payload = Json.decodeFromString<APIKeyCredentialPayload>(value.decodeToString())
            APIKeyCredential(payload.apiKey)
        }
    }