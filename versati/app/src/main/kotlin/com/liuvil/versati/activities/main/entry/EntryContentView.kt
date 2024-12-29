package com.liuvil.versati.activities.main.entry

import android.graphics.Color
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.liuvil.versati.framework.html.applyStyling

@Composable
internal fun EntryContentView(
    content: String,
    style: String
) {
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
                        applyStyling(content, style),
                        "text/html",
                        "UTF-8"
                    )
                }
            }
        )
    }
}
