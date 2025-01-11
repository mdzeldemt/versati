package com.liuvil.versati.framework.css

import androidx.compose.material3.ColorScheme

val ENVIRONMENT_CSS_VARIABLES = listOf(
    "--primary-color",
    "--on-primary-color",
    "--secondary-color",
    "--on-secondary-color",
    "--tertiary-color",
    "--on-tertiary-color",
    "--surface-color",
    "--on-surface-color",
)

fun getEnvironmentValue(
    name: String,
    colorScheme: ColorScheme
): String =
    when (name) {
        "--primary-color" -> colorScheme.primary.cssString
        "--on-primary-color" -> colorScheme.onPrimary.cssString
        "--secondary-color" -> colorScheme.secondary.cssString
        "--on-secondary-color" -> colorScheme.onSecondary.cssString
        "--tertiary-color" -> colorScheme.tertiary.cssString
        "--on-tertiary-color" -> colorScheme.onTertiary.cssString
        "--surface-color" -> colorScheme.surface.cssString
        "--on-surface-color" -> colorScheme.onSurface.cssString
        else -> throw IllegalArgumentException("Invalid variable name")
    }
