package com.liuvil.versati.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LIGHT_COLOR_SCHEME = lightColorScheme()
private val DARK_COLOR_SCHEME = darkColorScheme()

private val TYPOGRAPHY = Typography()

@Composable
fun Theme(
    content: @Composable () -> Unit,
) {
    val colorScheme =
        if (isSystemInDarkTheme())
            DARK_COLOR_SCHEME
        else
            LIGHT_COLOR_SCHEME

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TYPOGRAPHY,
        content = content
    )
}