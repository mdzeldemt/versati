package com.liuvil.versati.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class HeaderButton(
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun Header(
    title: String? = null,
    startButtons: List<HeaderButton> = emptyList(),
    endButtons: List<HeaderButton> = emptyList(),
    onClick: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(24.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = null
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            startButtons.map {
                HeaderButton(it)
            }

            Spacer(modifier = Modifier.weight(1f))

            endButtons.map {
                HeaderButton(it)
            }
        }

        title?.let {
            Text(
                text = it,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HeaderButton(button: HeaderButton) {
    IconButton(
        onClick = button.onClick,
        modifier = Modifier.aspectRatio(1f)
    ) {
        Icon(
            imageVector = button.icon,
            contentDescription = null
        )
    }
}
