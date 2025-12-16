package com.liuvil.versati.repository.api.auth

import javax.inject.Inject

class AuthContext @Inject constructor() {

    @Volatile private var credentials: Credentials? = null

    fun getCredentials(): Credentials?
        = credentials

    fun updateCredentials(credentials: Credentials?) {
        this.credentials = credentials
    }
}
