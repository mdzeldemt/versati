package com.liuvil.versati.framework.entry.content

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle

sealed interface EntryContentItem

data class EntryTextParagraphItem(
    val text: AnnotatedString
): EntryContentItem

data class EntryImageItem(
    val url: String
): EntryContentItem

fun buildEntryTextParagraphItem(
    text: String,
    spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    links: List<AnnotatedString.Range<LinkAnnotation.Url>>,
): EntryTextParagraphItem {
    val annotatedTextBuilder = AnnotatedString.Builder(text)
    spanStyles.forEach {
        annotatedTextBuilder.addStyle(it.item, it.start, it.end)
    }
    links.forEach {
        annotatedTextBuilder.addLink(it.item, it.start, it.end)
    }
    return EntryTextParagraphItem(
        text = annotatedTextBuilder.toAnnotatedString()
    )
}
