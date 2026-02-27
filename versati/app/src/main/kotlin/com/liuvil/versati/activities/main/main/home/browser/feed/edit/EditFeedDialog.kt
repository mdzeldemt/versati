package com.liuvil.versati.activities.main.main.home.browser.feed.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.liuvil.versati.components.form.menu.DropdownMenuField
import com.liuvil.versati.components.form.menu.DropdownMenuItem
import com.liuvil.versati.framework.string.isValidURL
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URL

internal data class Category(
    val id: Int,
    val title: String
)

internal data class SubmitData(
    val title: String,
    val feedUrl: URL,
    val categoryId: Int
)

@Composable
internal fun EditFeedDialog(
    initialTitle: String,
    initialFeedUrl: URL,
    initialCategoryId: Int,
    categories: List<Category>,
    onSubmit: (SubmitData) -> Unit,
    onRespond: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var feedUrl by remember { mutableStateOf(initialFeedUrl.toString()) }
    var categoryId by remember { mutableIntStateOf(initialCategoryId) }

    val isTitleError = title.isEmpty()
    val isFeedUrlError = !isValidURL(feedUrl)

    AlertDialog(
        onDismissRequest = onRespond,
        title = {
            Text("Edit a feed")
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
                    onSelectionChanged = { newValue ->
                        categoryId = newValue
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isTitleError && !isFeedUrlError,
                onClick = {
                    onRespond()
                    onSubmit(
                        SubmitData(
                            title = title,
                            feedUrl = feedUrl.toHttpUrl().toUrl(),
                            categoryId = categoryId
                        )
                    )
                }
            ) {
                Text("Update feed")
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
