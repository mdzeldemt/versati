package com.liuvil.versati.components

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable

@Composable
fun SmallActionButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    SmallFloatingActionButton(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        onClick = onClick
    ) {
        icon()
    }
}

@Composable
fun LargeActionButton(
    text: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = {
            if (text != null) {
                text()
            }
        },
        icon = {
            if (icon != null) {
                icon()
            }
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        onClick = onClick
    )
}