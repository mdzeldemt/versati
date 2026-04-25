package com.mdzeldemt.versati.activities.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mdzeldemt.versati.preferences.ColorScheme
import com.mdzeldemt.versati.preferences.PreferenceStore
import com.mdzeldemt.versati.theme.Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    @Inject
    lateinit var preferenceStore: PreferenceStore

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val colorScheme by preferenceStore.colorScheme.collectAsState(ColorScheme.SYSTEM)

            Theme(colorScheme) {
                Surface {
                    RootView()
                }
            }
        }
    }
}