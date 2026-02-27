package com.liuvil.versati.activities.main.main.home.browser.page

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly

@Composable
fun PageDialog(
    initialValue: Int,
    totalPages: Int,
    onSubmit: (Int) -> Unit,
    onRespond: () -> Unit
) {
    var value by remember {
        "$initialValue".let {
            mutableStateOf(TextFieldValue(it, TextRange(it.length)))
        }
    }

    AlertDialog(
        onDismissRequest = onRespond,
        title = { Text("Go to page") },
        text = {
            TextField(
                value = value,
                onValueChange = {
                    if (it.text.isEmpty() ||
                        it.text.isDigitsOnly() && it.text.toInt() in 1..totalPages)
                        value = it
                },
                suffix = {
                    Text("/ $totalPages")
                },
                keyboardOptions = KeyboardOptions.Default
                    .copy(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(
                enabled = value.text.isNotEmpty(),
                onClick = {
                    onSubmit(value.text.toInt())
                    onRespond()
                }
            ) {
                Text("Go")
            }
        },
        dismissButton = {
            TextButton(onClick = { onRespond() }) {
                Text("Close")
            }
        }
    )
}
