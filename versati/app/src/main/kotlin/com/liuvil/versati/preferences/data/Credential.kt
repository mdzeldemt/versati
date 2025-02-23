package com.liuvil.versati.preferences.data

interface Credential

interface MinifluxBasicCredential: Credential {
    val username: String
    val password: String
}

interface MinifluxAPIKeyCredential: Credential {
    val apiKey: String
}
