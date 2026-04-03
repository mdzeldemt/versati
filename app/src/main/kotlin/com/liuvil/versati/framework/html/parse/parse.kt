package com.liuvil.versati.framework.html.parse

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

fun parseDocument(
    html: String
): HtmlDocument {
    val rootElement = Jsoup.parse(html).body()
    return HtmlDocument(
        content = parseBlocks(rootElement)
    )
}

private fun parseBlocks(
    element: Element,
    parentLinkUrl: String? = null
): List<HtmlBlock> {
    val blocks = mutableListOf<HtmlBlock>()
    val buffer = StringBuilder()

    fun flushBuffer() {
        val text = buffer.toString().trim()
        val annotated = parseSpanned(text).toAnnotatedString().trimLineBreaks()
        if (annotated.isNotEmpty()) {
            blocks.add(HtmlBlock.Paragraph(annotated))
        }
        buffer.clear()
    }

    fun traverse(
        element: Element,
        parentLinkUrl: String?
    ) {
        val activeLinkUrl =
            if (element.tagName() == "a")
                element.attr("href")
            else
                parentLinkUrl

        when (element.tagName()) {
            "h1", "h2", "h3" -> {
                flushBuffer()

                val paragraphType =
                    when (element.tagName()) {
                        "h1" -> HtmlBlock.Paragraph.Type.H1
                        "h2" -> HtmlBlock.Paragraph.Type.H2
                        else -> HtmlBlock.Paragraph.Type.H3
                    }

                blocks.add(
                    HtmlBlock.Paragraph(
                        parseSpanned(element.html()).toAnnotatedString(),
                        paragraphType
                    )
                )
            }

            "ul", "ol" -> {
                flushBuffer()

                element.select("> li")
                    .forEachIndexed { index, li ->
                        val children = parseBlocks(li, activeLinkUrl)
                        if (element.tagName() == "ol") {
                            blocks.add(HtmlBlock.OrderedItem(children, index + 1))
                        } else {
                            blocks.add(HtmlBlock.BulletItem(children))
                        }
                    }
            }

            "img" -> {
                flushBuffer()

                blocks.add(HtmlBlock.Image(element.attr("src"), activeLinkUrl))
            }

            "table" -> {
                flushBuffer()

                blocks.add(parseTableBlock(element, activeLinkUrl))
            }

            "p", "div", "section", "figure" -> {
                if (buffer.isNotEmpty()) {
                    flushBuffer()
                }

                element.childNodes()
                    .forEach { node ->
                        if (node is Element) traverse(node, activeLinkUrl)
                        else if (node is TextNode) buffer.append(node.outerHtml())
                    }

                flushBuffer()
            }

            "br" -> buffer.append("<br>")

            else -> {
                if (element.childrenSize() > 0) {
                    buffer.append("<${element.tagName()}${element.attributes().html()}>")
                    element.childNodes()
                        .forEach { node ->
                            if (node is Element) traverse(node, activeLinkUrl)
                            else if (node is TextNode) buffer.append(node.outerHtml())
                        }
                    buffer.append("</${element.tagName()}>")
                } else {
                    buffer.append(element.outerHtml())
                }
            }
        }
    }

    element.childNodes()
        .forEach { node ->
            if (node is Element) traverse(node, parentLinkUrl)
            else if (node is TextNode) buffer.append(node.outerHtml())
        }

    flushBuffer()

    return blocks
}

private fun parseTableBlock(
    tableElement: Element,
    linkUrl: String?
): HtmlBlock.Table {
    val rows = tableElement.select("tr")
        .map { tr ->
            val cells = tr.select("th, td")
                .map { cell ->
                    HtmlBlock.Table.Cell(
                        parseBlocks(cell, linkUrl)
                    )
                }
            HtmlBlock.Table.Row(cells)
        }
    return HtmlBlock.Table(rows)
}

private fun parseSpanned(
    html: String
): Spanned =
    HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)

private fun Spanned.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        val text = this@toAnnotatedString.toString()
        append(text)

        getSpans(0, length, Any::class.java).forEach { span ->
            val start = getSpanStart(span)
            val end = getSpanEnd(span)

            if (start < 0 || end <= start) {
                return@forEach
            }

            when (span) {
                is StyleSpan -> {
                    when (span.style) {
                        Typeface.BOLD ->
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.Bold),
                                start,
                                end
                            )

                        Typeface.ITALIC ->
                            addStyle(
                                SpanStyle(fontStyle = FontStyle.Italic),
                                start,
                                end
                            )

                        Typeface.BOLD_ITALIC ->
                            addStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                ),
                                start,
                                end
                            )
                    }
                }

                is UnderlineSpan ->
                    addStyle(
                        SpanStyle(textDecoration = TextDecoration.Underline),
                        start,
                        end
                    )

                is StrikethroughSpan ->
                    addStyle(
                        SpanStyle(textDecoration = TextDecoration.LineThrough),
                        start,
                        end
                    )

                is TypefaceSpan -> {
                    if (span.family == "monospace") {
                        addStyle(
                            SpanStyle(fontFamily = FontFamily.Monospace),
                            start,
                            end
                        )
                    }
                }

                is URLSpan -> {
                    addStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        ),
                        start,
                        end
                    )

                    addLink(
                        LinkAnnotation.Url(
                            span.url,
                            TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline))
                        ),
                        start = start,
                        end = end
                    )
                }

                is ForegroundColorSpan ->
                    addStyle(
                        SpanStyle(color = Color(span.foregroundColor)),
                        start,
                        end
                    )

                is BackgroundColorSpan ->
                    addStyle(
                        SpanStyle(background = Color(span.backgroundColor)),
                        start,
                        end
                    )
            }
        }
    }
}

private fun AnnotatedString.trimLineBreaks(): AnnotatedString {
    val startIndex = indexOfFirst { !it.isWhitespace() }
    val endIndex = indexOfLast { !it.isWhitespace() }
    if (startIndex == -1 || endIndex == -1) {
        return AnnotatedString("")
    }
    return subSequence(startIndex, endIndex + 1)
}
