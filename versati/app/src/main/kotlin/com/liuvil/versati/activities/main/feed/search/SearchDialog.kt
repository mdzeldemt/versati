package com.liuvil.versati.activities.main.feed.search

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.liuvil.versati.components.ConfirmationDialog

@Composable
fun SearchDialog(
    initialTerm: String,
    onSubmit: (String) -> Unit,
    onClose: () -> Unit = {},
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

    ConfirmationDialog(
        title = { Text("Search for entries") },
        body = {
            Column {
                Text("Enter a search term below.")

                TextField(
                    value = term,
                    onValueChange = { term = it }
                )
            }
        },
        confirmText = "Search",
        dismissText = "Close",
        onConfirm = {
            onSubmit(term.text)
        },
        onDismiss = onClose,
        onRespond = onRespond
    )
}