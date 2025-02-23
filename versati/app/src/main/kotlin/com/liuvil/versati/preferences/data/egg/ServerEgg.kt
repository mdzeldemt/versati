package com.liuvil.versati.preferences.data.egg

import java.net.URL

data class ServerEgg(
    val name: String,
    val baseURL: URL,
    val credential: CredentialEgg
)
