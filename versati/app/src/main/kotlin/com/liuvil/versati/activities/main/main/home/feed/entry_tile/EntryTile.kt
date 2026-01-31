package com.liuvil.versati.activities.main.main.home.feed.entry_tile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.liuvil.versati.components.WrapperLayout
import java.net.URL
import java.time.Duration

@Composable
fun EntryTile(
    title: String,
    feedTitle: String,
    timeSincePublished: Duration,
    content: String,
    imageURL: URL? = null,
    isRead: Boolean,
    onClick: () -> Unit
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

    Box(
        Modifier
            .clickable { onClick() }
            .padding(10.dp)
            .apply {
                if (isRead) alpha(0.5f)
            }
    ) {
        imageURL?.let {
            WrapperLayout(
                pivotSize = 100.dp,
                pivotContent = {
                    EntryImage(it)
                },
                wrapperContent = { text() }
            )
        } ?: Column(content = { text() })
    }
}

@Composable
private fun EntryImage(
    imageUrl: URL
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl.toString())
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1.2f)
            .padding(start = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant
                    .copy(alpha = 0.25f)
            ),
        error = {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.BrokenImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(0.5f),
                )
            }
        }
    )
}

private fun toHumanReadable(
    duration: Duration
): String {
    return if (duration < Duration.ofMinutes(60)) {
        "${duration.toMinutes()}min"
    } else if (duration < Duration.ofHours(24)) {
        "${duration.toHours()}h"
    } else if (duration < Duration.ofDays(365)) {
        "${duration.toDays()}d"
    } else {
        "${duration.toDays() / 365}y"
    }
}