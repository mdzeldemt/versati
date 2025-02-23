package com.liuvil.versati.preferences.database.data.conversion

import com.liuvil.versati.preferences.data.egg.CredentialEgg
import com.liuvil.versati.preferences.data.egg.MinifluxAPIKeyCredentialEgg
import com.liuvil.versati.preferences.data.egg.MinifluxBasicCredentialEgg
import com.liuvil.versati.preferences.database.data.Credential
import com.liuvil.versati.preferences.database.data.MinifluxAPIKeyCredential
import com.liuvil.versati.preferences.database.data.MinifluxBasicCredential

fun CredentialEgg.toDatabase(): Credential =
    when (this) {
        is MinifluxBasicCredentialEgg ->
            MinifluxBasicCredential(
                username = username,
                password = password
            )

        is MinifluxAPIKeyCredentialEgg ->
            MinifluxAPIKeyCredential(
                apiKey = apiKey
            )
    }