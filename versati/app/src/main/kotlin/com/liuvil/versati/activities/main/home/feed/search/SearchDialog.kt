package com.liuvil.versati.activities.main.home.feed.search

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
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun SearchDialog(
    initialTerm: String,
    onSubmit: (String) -> Unit,
    onRespond: () -> Unit
) {
    var term by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialTerm,
                selection = TextRange(initialTerm.length)
            )
        )
    }

    AlertDialog(
        onDismissRequest = onRespond,
        title = { Text("Search for entries") },
        text = {
            TextField(
                value = term,
                onValueChange = { term = it }
            )
        },
        confirmButton = {
            TextButton(
                enabled = term.text.isNotEmpty(),
                onClick = {
                    onSubmit(term.text)
                    onRespond()
                }
            ) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = { onRespond() }) {
                Text("Close")
            }
        }
    )
}