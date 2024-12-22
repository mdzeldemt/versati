package com.liuvil.versati.api

sealed class MinifluxAuthenticationMethod

data class MinifluxBasicAuthentication(
    val username: String,
    val password: String
): MinifluxAuthenticationMethod()

data class MinifluxApiKeyAuthentication(
    val apiKey: String
): MinifluxAuthenticationMethod()