package com.liuvil.versati.preferences.database.data

import com.liuvil.versati.preferences.data.MinifluxAPIKeyCredential
import com.liuvil.versati.preferences.data.MinifluxBasicCredential
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Credential: com.liuvil.versati.preferences.data.Credential

@Serializable
@SerialName("miniflux_basic")
data class MinifluxBasicCredential(
    override val username: String,
    override val password: String
): Credential(), MinifluxBasicCredential

@Serializable
@SerialName("miniflux_api_key")
data class MinifluxAPIKeyCredential(
    override val apiKey: String
): Credential(), MinifluxAPIKeyCredential
