package com.liuvil.versati.activities.main.main.home.reader.content

import android.graphics.Color
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.liuvil.versati.framework.css.ENVIRONMENT_CSS_VARIABLES
import com.liuvil.versati.framework.css.buildCSSBlock
import com.liuvil.versati.framework.css.getEnvironmentValue
import com.liuvil.versati.framework.html.buildStyleElement
import org.jsoup.Jsoup

@Composable
internal fun EntryContentView(
    content: String,
    stylesheet: String
) {
    val rootStylesheet = MaterialTheme.colorScheme.let { colorScheme ->
        buildCSSBlock(
            ":root",
            ENVIRONMENT_CSS_VARIABLES
                .associateWith { getEnvironmentValue(it, colorScheme) }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    clipToOutline = true

                    setBackgroundColor(Color.TRANSPARENT)

                    settings.apply {
                        standardFontFamily = "sans"
                    }

                    loadData(
                        listOf(rootStylesheet, stylesheet)
                            .fold(content) { it, stylesheet ->
                                applyStylesheet(it, stylesheet)
                            },
                        "text/html",
                        "UTF-8"
                    )
                }
            }
        )
    }
}

private fun applyStylesheet(
    content: String,
    stylesheet: String
): String =
    Jsoup.parse(content)
        .appendChild(buildStyleElement(stylesheet))
        .html()
