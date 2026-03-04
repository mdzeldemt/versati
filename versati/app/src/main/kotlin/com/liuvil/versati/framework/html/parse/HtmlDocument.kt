package com.liuvil.versati.framework.html.parse

import androidx.compose.ui.text.AnnotatedString

data class HtmlDocument(
    val content: List<HtmlBlock>
)

sealed class HtmlBlock {
    data class Paragraph(
        val text: AnnotatedString,
        val type: Type = Type.Body
    ): HtmlBlock() {
        enum class Type {
            Body,
            H1,
            H2,
            H3
        }
    }

    data class Image(
        val url: String,
        val linkUrl: String? = null
    ): HtmlBlock()

    data class Table(
        val rows: List<Row>
    ): HtmlBlock() {
        data class Row(
            val cells: List<Cell>
        )

        data class Cell(
            val content: List<HtmlBlock>
        )
    }

    data class BulletItem(
        val content: List<HtmlBlock>
    ): HtmlBlock()

    data class OrderedItem(
        val content: List<HtmlBlock>,
        val index: Int
    ): HtmlBlock()
}
