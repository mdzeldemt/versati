package com.liuvil.versati.components.scaffold.action

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BackButton(
    icon: ImageVector = Icons.AutoMirrored.Default.ArrowBack,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )

        BackHandler {
            onClick()
        }
    }
}