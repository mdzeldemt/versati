package com.liuvil.versati.activities.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.navigation.NavigationRoot
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    @Inject
    lateinit var minifluxApi: MinifluxApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NavigationRoot()
        }
    }
}