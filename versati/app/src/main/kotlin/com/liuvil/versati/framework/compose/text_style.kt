package com.liuvil.versati.framework.compose

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

fun TextStyle.scaledTo(
    factor: Float
) = copy(
    fontSize = fontSize * factor
)

fun TextStyle.bold() = copy(
    fontWeight = FontWeight.Bold
)