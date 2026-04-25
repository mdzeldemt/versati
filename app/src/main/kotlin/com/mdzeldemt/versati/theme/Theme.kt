package com.mdzeldemt.versati.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.mdzeldemt.versati.preferences.ColorScheme

@Composable
fun Theme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme =
            when (colorScheme) {
                ColorScheme.SYSTEM ->
                    if (isSystemInDarkTheme())
                        darkColorScheme()
                    else
                        lightColorScheme()

                ColorScheme.DARK ->
                    darkColorScheme()

                ColorScheme.LIGHT ->
                    lightColorScheme()
            }
    ) {
        content()
    }
}