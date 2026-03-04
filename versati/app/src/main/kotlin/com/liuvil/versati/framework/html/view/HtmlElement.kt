package com.liuvil.versati.framework.html.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.liuvil.versati.framework.html.parse.HtmlBlock

@Composable
fun HtmlElement(
    block: HtmlBlock
) {
    when (block) {
        is HtmlBlock.Paragraph -> HtmlParagraph(block)
        is HtmlBlock.Image -> HtmlImage(block)
        is HtmlBlock.Table -> HtmlTable(block)
        is HtmlBlock.BulletItem -> HtmlBulletItem(block)
        is HtmlBlock.OrderedItem -> HtmlOrderedItem(block)
    }
}

@Composable
private fun HtmlParagraph(
    block: HtmlBlock.Paragraph
) {
    val style =
        when (block.type) {
            HtmlBlock.Paragraph.Type.H1 ->
                MaterialTheme.typography.headlineLarge

            HtmlBlock.Paragraph.Type.H2 ->
                MaterialTheme.typography.headlineMedium

            HtmlBlock.Paragraph.Type.H3 ->
                MaterialTheme.typography.headlineSmall

            HtmlBlock.Paragraph.Type.Body ->
                MaterialTheme.typography.bodyLarge
        }

    Text(
        text = block.text,
        style = style
    )
}

@Composable
private fun HtmlImage(
    block: HtmlBlock.Image
) {
    AsyncImage(
        model = block.url,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
private fun HtmlTable(
    table: HtmlBlock.Table
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray)
    ) {
        table.rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1f
                        )
                    }
            ) {
                row.cells.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                        ) {
                            cell.content.forEach { child ->
                                HtmlElement(child)
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun HtmlBulletItem(
    block: HtmlBlock.BulletItem
) {
    Row(
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Text(
            text = "• ",
            modifier = Modifier.width(20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            block.content.forEach { child ->
                HtmlElement(child)
            }
        }
    }
}

@Composable
private fun HtmlOrderedItem(
    block: HtmlBlock.OrderedItem
) {
    Row(
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Text(
            text = "${block.index}. ",
            modifier = Modifier.width(20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            block.content.forEach { child ->
                HtmlElement(child)
            }
        }
    }
}