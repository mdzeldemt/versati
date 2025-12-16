package com.liuvil.versati.repository.api.auth

sealed interface Credentials {

    data class Basic(
        val username: String,
        val password: String
    ): Credentials

    data class APIKey(
        val apiKey: String
    ): Credentials
}