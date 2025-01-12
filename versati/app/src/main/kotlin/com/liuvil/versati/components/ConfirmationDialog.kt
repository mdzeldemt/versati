package com.liuvil.versati.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(
    titleText: String,
    bodyText: String,
    confirmText: String = "Yes",
    dismissText: String = "No",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {},
    onRespond: () -> Unit = {}
) {
    ConfirmationDialog(
        title = { Text(titleText) },
        body = { Text(bodyText) },
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        onRespond = onRespond
    )
}

@Composable
fun ConfirmationDialog(
    title: @Composable () -> Unit,
    body: @Composable () -> Unit,
    confirmText: String = "Yes",
    dismissText: String = "No",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {},
    onRespond: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = title,
        text = body,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onRespond()
            }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onRespond()
            }) {
                Text(dismissText)
            }
        }
    )
}
