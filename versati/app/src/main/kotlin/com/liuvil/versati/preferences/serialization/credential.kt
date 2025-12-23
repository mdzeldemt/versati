package com.liuvil.versati.preferences.serialization

import com.liuvil.versati.preferences.data.APIKeyCredentials
import com.liuvil.versati.preferences.data.BasicCredentials
import com.liuvil.versati.preferences.data.Credentials
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
    credentials: Credentials
): ByteArray =
    when (credentials) {
        is BasicCredentials ->
            Json.encodeToString(
                BasicCredentialPayload(credentials.username, credentials.password)
            ).encodeToByteArray()

        is APIKeyCredentials ->
            Json.encodeToString(
                APIKeyCredentialPayload(credentials.apiKey)
            ).encodeToByteArray()
    }

fun deserialize(
    value: ByteArray,
    credentialType: CredentialType
): Credentials =
    when (credentialType) {
        CredentialType.BASIC -> {
            val payload = Json.decodeFromString<BasicCredentialPayload>(value.decodeToString())
            BasicCredentials(payload.username, payload.password)
        }

        CredentialType.API_KEY -> {
            val payload = Json.decodeFromString<APIKeyCredentialPayload>(value.decodeToString())
            APIKeyCredentials(payload.apiKey)
        }
    }