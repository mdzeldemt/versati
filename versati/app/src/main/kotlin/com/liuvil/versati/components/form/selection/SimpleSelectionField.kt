package com.liuvil.versati.components.form.selection

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Item<T>(
    val key: T,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleSelectionField(
    items: List<Item<T>>,
    selectedKey: T,
    onSelectedKeyChange: (T) -> Unit,
    label: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = getItemLabel(selectedKey, items),
            onValueChange = {},
            readOnly = true,
            label = label,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it.label) },
                    onClick = {
                        expanded = false
                        onSelectedKeyChange(it.key)
                    }
                )
            }
        }
    }
}

private fun <T> getItemLabel(
    key: T,
    items: List<Item<T>>,
): String =
    items.firstOrNull { it.key == key }
        ?.label
        ?: ""