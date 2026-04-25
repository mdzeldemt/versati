package com.mdzeldemt.versati.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(
    titleText: String,
    bodyText: String,
    confirmText: String = "OK",
    onConfirm: () -> Unit
) {
    ErrorDialog(
        title = { Text(titleText) },
        body = { Text(bodyText) },
        confirmText = confirmText,
        onConfirm = onConfirm
    )
}

@Composable
fun ErrorDialog(
    title: @Composable () -> Unit,
    body: @Composable () -> Unit,
    confirmText: String = "OK",
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = title,
        text = body,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(confirmText)
            }
        }
    )
}
