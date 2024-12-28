package com.liuvil.versati.framework.entry.content

import android.text.Html
import android.text.Spanned
import android.text.style.ImageSpan
import com.liuvil.versati.framework.annotatedstring.extractLinkAnnotations
import com.liuvil.versati.framework.annotatedstring.extractSpanStyleAnnotations
import com.liuvil.versati.framework.range.IntRangeBuilder

fun parseContentItems(
    html: String
): List<EntryContentItem> {
    val spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)

    val result = mutableListOf<EntryContentItem>()
    val currentParagraph = IntRangeBuilder()
    spanned.forEachIndexed { index, char ->
        if (char == '\n' || index == spanned.lastIndex) {
            if (index == spanned.lastIndex && char != '\n') {
                currentParagraph.extendTo(index)
            }

            if (!currentParagraph.isEmpty()) {
                val range = currentParagraph.toIntRange()
                val textParagraphItem = parseTextParagraphItem(spanned, range.first, range.last)
                if (textParagraphItem.text.isNotBlank()) {
                    result.add(textParagraphItem)
                }
            }

            currentParagraph.clear()
            return@forEachIndexed
        }

        parseImageItemOrNull(spanned, index)?.let { imageItem ->
            result.add(imageItem)
            return@forEachIndexed
        }

        currentParagraph.extendTo(index)
    }

    return result
}

private fun parseTextParagraphItem(
    spanned: Spanned,
    start: Int,
    end: Int
): EntryTextParagraphItem =
    buildEntryTextParagraphItem(
        text = spanned.substring(start, end),
        spanStyles = extractSpanStyleAnnotations(spanned, start, end),
        links = extractLinkAnnotations(spanned, start, end)
    )

private fun parseImageItemOrNull(
    spanned: Spanned,
    position: Int
): EntryImageItem? =
    spanned.getSpans(position, position + 1, ImageSpan::class.java).firstOrNull()?.let { imageSpan ->
        imageSpan.source?.let { source ->
            EntryImageItem(url = source)
        }
    }
