package com.liuvil.versati.framework.html.parse

fun HtmlDocument.descendants(): List<HtmlBlock> =
    content.flatMap { block ->
        listOf(block) + block.descendants()
    }

fun HtmlBlock.descendants(): List<HtmlBlock> =
    when (this) {
        is HtmlBlock.Paragraph
            -> emptyList()

        is HtmlBlock.Image
            -> emptyList()

        is HtmlBlock.BulletItem ->
            content.flatMap { listOf(it) + it.descendants() }

        is HtmlBlock.OrderedItem ->
            content.flatMap { listOf(it) + it.descendants() }

        is HtmlBlock.Table ->
            rows
                .flatMap { row ->
                    row.cells
                }
                .flatMap { cell ->
                    cell.content.flatMap {
                        listOf(it) + it.descendants()
                    }
                }
    }
