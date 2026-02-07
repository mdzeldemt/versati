package com.liuvil.versati.components.form.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

data class DropdownMenuItem<T>(
    val key: T,
    val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenuField(
    title: String,
    items: List<DropdownMenuItem<T>>,
    selection: T?,
    isError: Boolean = false,
    onSelectionChanged: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = items
                .firstOrNull { it.key == selection } ?.title
                ?: "None selected",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(title)
            },
            trailingIcon = {
                if (expanded) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropUp,
                        contentDescription = null
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            isError = isError
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it.title)
                    },
                    onClick = {
                        onSelectionChanged(it.key)
                        expanded = false
                    }
                )
            }
        }
    }
}