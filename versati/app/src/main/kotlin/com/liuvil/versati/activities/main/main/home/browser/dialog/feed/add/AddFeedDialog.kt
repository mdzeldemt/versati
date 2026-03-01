package com.liuvil.versati.activities.main.main.home.browser.dialog.feed.add

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
import com.liuvil.versati.components.form.menu.DropdownMenuField
import com.liuvil.versati.components.form.menu.DropdownMenuItem
import com.liuvil.versati.framework.string.isValidUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URL

internal data class Category(
    val id: Int,
    val title: String
)

internal data class SubmitData(
    val feedUrl: URL,
    val categoryId: Int
)

@Composable
internal fun AddFeedDialog(
    categories: List<Category>,
    onSubmit: (SubmitData) -> Unit,
    onRespond: () -> Unit
) {
    var feedUrl by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<Int?>(null) }

    val isFeedUrlError = !isValidUrl(feedUrl)
    val isCategoryIdError = categoryId == null

    AlertDialog(
        onDismissRequest = onRespond,
        title = {
            Text("Add a feed")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                TextField(
                    value = feedUrl,
                    label = {
                        Text("Feed URL")
                    },
                    isError = isFeedUrlError,
                    onValueChange = { newValue ->
                        feedUrl = newValue
                    }
                )

                DropdownMenuField(
                    title = "Category",
                    items = categories.map { category ->
                        DropdownMenuItem(
                            key = category.id,
                            title = category.title
                        )
                    },
                    selection = categoryId,
                    isError = isCategoryIdError,
                    onSelectionChanged = { newValue ->
                        categoryId = newValue
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isFeedUrlError && !isCategoryIdError,
                onClick = {
                    onRespond()
                    onSubmit(
                        SubmitData(
                            feedUrl = feedUrl.toHttpUrl().toUrl(),
                            categoryId = categoryId!!
                        )
                    )
                }
            ) {
                Text("Add feed")
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
