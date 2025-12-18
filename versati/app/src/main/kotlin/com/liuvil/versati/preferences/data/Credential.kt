package com.liuvil.versati.preferences.data

sealed interface Credential

data class BasicCredential(
    val username: String,
    val password: String
): Credential

data class APIKeyCredential(
    val apiKey: String
): Credential
