package com.liuvil.versati.activities.main.main.home.category.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

internal data class SubmitData(
    val title: String
)

@Composable
internal fun EditCategoryDialog(
    initialTitle: String,
    onSubmit: (SubmitData) -> Unit,
    onRespond: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }

    val isTitleError = title.isEmpty()

    AlertDialog(
        onDismissRequest = onRespond,
        title = {
            Text("Edit a category")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                TextField(
                    value = title,
                    label = {
                        Text("Title")
                    },
                    isError = isTitleError,
                    onValueChange = { newValue ->
                        title = newValue
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isTitleError,
                onClick = {
                    onRespond()
                    onSubmit(SubmitData(title))
                }
            ) {
                Text("Update category")
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
