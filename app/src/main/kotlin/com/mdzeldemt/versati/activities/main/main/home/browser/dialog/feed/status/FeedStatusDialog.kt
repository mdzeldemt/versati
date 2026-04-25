package com.mdzeldemt.versati.activities.main.main.home.browser.dialog.feed.status

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.mdzeldemt.versati.framework.date.formatHumanReadableLong
import java.time.OffsetDateTime

@Composable
fun FeedStatusDialog(
    checkedAt: OffsetDateTime,
    nextCheckAt: OffsetDateTime,
    parsingErrorCount: Int,
    parsingErrorMessage: String?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Feed status")
        },
        text = {
            Text(
                getFeedStatusText(
                    checkedAt = checkedAt,
                    nextCheckAt = nextCheckAt,
                    parsingErrorCount = parsingErrorCount,
                    parsingErrorMessage = parsingErrorMessage
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private fun getFeedStatusText(
    checkedAt: OffsetDateTime,
    nextCheckAt: OffsetDateTime,
    parsingErrorCount: Int,
    parsingErrorMessage: String?
): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("checked at: ")
    }
    append(checkedAt.formatHumanReadableLong())

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nnext check at: ")
    }
    append(nextCheckAt.formatHumanReadableLong())

    if (parsingErrorCount > 0) {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\ntotal parsing errors: ")
        }
        append("$parsingErrorCount")
    }

    if (parsingErrorMessage != null) {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\nlatest parsing error: ")
        }
        append(parsingErrorMessage)
    }
}