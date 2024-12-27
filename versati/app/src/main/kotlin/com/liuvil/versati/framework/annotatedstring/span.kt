package com.liuvil.versati.framework.annotatedstring

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

fun extractSpanStyleAnnotations(
    spanned: Spanned,
    start: Int,
    end: Int
): List<AnnotatedString.Range<SpanStyle>> =
    spanned.getSpans(start, end, Any::class.java)
        .mapNotNull { span ->
            val annotationStart = spanned.getSpanStart(span) - start
            val annotationEnd = spanned.getSpanEnd(span) - start
            when (span) {
                is StyleSpan -> {
                    val fontWeight =
                        if (arrayOf(Typeface.BOLD, Typeface.BOLD_ITALIC).contains(span.style))
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    val fontStyle =
                        if (arrayOf(Typeface.ITALIC, Typeface.BOLD_ITALIC).contains(span.style))
                            FontStyle.Italic
                        else
                            FontStyle.Normal
                    AnnotatedString.Range(
                        SpanStyle(fontWeight = fontWeight, fontStyle = fontStyle),
                        start = annotationStart,
                        end = annotationEnd
                    )
                }

                is UnderlineSpan ->
                    AnnotatedString.Range(
                        SpanStyle(textDecoration = TextDecoration.Underline),
                        start = annotationStart,
                        end = annotationEnd
                    )

                is URLSpan -> AnnotatedString.Range(
                    SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = annotationStart,
                    end = annotationEnd
                )

                else -> null
            }
        }

fun extractLinkAnnotations(
    spanned: Spanned,
    start: Int,
    end: Int
): List<AnnotatedString. Range<LinkAnnotation. Url>> =
    spanned.getSpans(start, end, URLSpan::class.java).map { span ->
        AnnotatedString.Range(
            LinkAnnotation.Url(span.url),
            start = spanned.getSpanStart(span) - start,
            end = spanned.getSpanEnd(span) - start
        )
    }