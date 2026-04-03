package com.liuvil.versati.framework.css

import androidx.compose.ui.graphics.Color

val Color.cssString: String
    get() = "rgb(${(red * 255).toInt()}, ${(green * 255).toInt()}, ${(blue * 255).toInt()})"