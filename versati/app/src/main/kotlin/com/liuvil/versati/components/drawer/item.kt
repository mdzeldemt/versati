package com.liuvil.versati.components.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val DRAWER_ICON_SIZE = 20.dp
private val HEADER_PADDING = 12.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DrawerItem(
    label: @Composable () -> Unit,
    icon: (@Composable () -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(
                color =
                    if (selected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        Color.Transparent
                    }
            )
            .run {
                if (onLongClick != null) {
                    combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                } else {
                    clickable {
                        onClick()
                    }
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (icon != null) {
                icon()
            }

            Spacer(Modifier.width(16.dp))

            label()

            Spacer(Modifier.width(16.dp))

            if (badge != null) {
                badge()
            }
        }
    }
}

@Composable
internal fun DrawerItemGroup(
    label: @Composable () -> Unit,
    selected: Boolean,
    expanded: Boolean,
    badge: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column {
        DrawerItem(
            label = {
                label()
            },
            icon = {
                Icon(
                    imageVector =
                        if (expanded)
                            Icons.Default.KeyboardArrowDown
                        else
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        onClick = {
                            onToggle()
                        },
                        indication = null,
                        interactionSource = null
                    )
                )
            },
            badge = {
                if (badge != null) {
                    badge()
                }
            },
            selected = selected,
            onClick = onClick,
            onLongClick = onLongClick
        )

        if (expanded) {
            Column(
                Modifier.padding(start = 24.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun DrawerItemIcon(
    imageVector: ImageVector
) {
    Box(Modifier.size(DRAWER_ICON_SIZE)) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }
}

@Composable
internal fun DrawerItemIcon(
    imageBitmap: ImageBitmap
) {
    Box(Modifier.size(DRAWER_ICON_SIZE)) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
internal fun DrawerItemLabel(
    text: String
) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun DrawerSectionHeader(
    title: String,
    buttons: (@Composable () -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(HEADER_PADDING)
        )

        if (buttons != null) {
            Row {
                buttons()
            }
        }
    }
}

@Composable
internal fun DrawerSectionHeaderButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(HEADER_PADDING)
                .size(20.dp)
        )
    }
}