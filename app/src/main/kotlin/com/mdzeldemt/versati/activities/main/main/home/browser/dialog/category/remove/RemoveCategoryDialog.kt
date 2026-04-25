package com.mdzeldemt.versati.activities.main.main.home.browser.dialog.category.remove

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
internal fun RemoveCategoryDialog(
    title: String,
    onConfirm: () -> Unit,
    onRespond: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onRespond,
        title = {
            Text("Remove a category")
        },
        text = {
            Text(
                text = buildAnnotatedString {
                    append("Are you sure you want to remove the category ")

                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(title)
                    }

                    append("? ")

                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("This action cannot be undone.")
                    }
                }
            )
        },
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Remove category")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onRespond
            ) {
                Text("Cancel")
            }
        }
    )
}
