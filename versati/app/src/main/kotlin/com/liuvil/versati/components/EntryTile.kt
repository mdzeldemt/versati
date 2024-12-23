package com.liuvil.versati.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.net.URL
import java.time.Duration

@Composable
fun EntryTile(
    title: String,
    feedTitle: String,
    timeSincePublished: Duration,
    content: String,
    imageUrl: URL? = null,
    imagePadding: Dp = 8.dp,
    imageCornerRadius: Dp = 4.dp
) {
    val text = @Composable {
        Text(title, fontWeight = FontWeight.Bold, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text("$feedTitle / ${toHumanReadable(timeSincePublished)}")

        content.trim().let {
            if (it.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(it, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }

    imageUrl?.let {
        WrapperLayout(
            pivotSize = 100.dp,
            pivotContent = {
                AsyncImage(
                    model = it.toString(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1.2f)
                        .padding(start = imagePadding, bottom = imagePadding)
                        .clip(RoundedCornerShape(imageCornerRadius))
                )
            },
            wrapperContent = { text() }
        )
    } ?: Column(content = { text() })
}

private fun toHumanReadable(
    duration: Duration
): String {
    return if (duration < Duration.ofMinutes(60)) {
        "${duration.toMinutes()}min"
    } else if (duration < Duration.ofHours(24)) {
        "${duration.toHours()}h"
    } else {
        "${duration.toDays()}d"
    }
}