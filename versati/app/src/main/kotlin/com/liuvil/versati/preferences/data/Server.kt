package com.liuvil.versati.preferences.data

import java.net.URL

interface Server {
    val id: Int
    val name: String
    val baseURL: URL
    val credential: Credential
}
