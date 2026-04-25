package com.mdzeldemt.versati.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun BlockingBox(
    isBlocking: Boolean,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    content()

    if (isBlocking) {
        Box(
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        // Do nothing
                    }
                )
        )
    }
}
