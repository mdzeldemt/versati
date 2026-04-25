package com.mdzeldemt.versati.components.menu.modal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class ModalMenuItem(
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun ModalMenu(
    items: List<ModalMenuItem>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                items(items) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                it.onClick()
                                onDismiss()
                            }
                    ) {
                        Text(
                            text = it.title,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}