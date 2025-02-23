package com.liuvil.versati.preferences.data.egg

sealed class CredentialEgg

data class MinifluxBasicCredentialEgg(
    val username: String,
    val password: String
): CredentialEgg()

data class MinifluxAPIKeyCredentialEgg(
    val apiKey: String
): CredentialEgg()
