package com.liuvil.versati.preferences

sealed interface Credentials

data class BasicCredentials(
    val username: String,
    val password: String
): Credentials

data class APIKeyCredentials(
    val apiKey: String
): Credentials
