package com.liuvil.versati.activities.main.entry

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.liuvil.versati.framework.viewmodel.bindViewModel
import org.jsoup.Jsoup

// TODO: Move to separate package
data class ReplacementRule(
    val attributeName: String,
    val attributeValue: String
)

val whitelistRules = mapOf(
    "html" to emptySet(),
    "body" to emptySet(),
    "p" to emptySet(),
    "span" to emptySet(),
    "a" to setOf("href"),
    "img" to setOf("src")
)

val replacementRules = mapOf(
    "img" to listOf(ReplacementRule("style", "max-width: 100%;"))
)

@SuppressLint("NewApi")
@Composable
fun EntryView(
    id: Int
) {
    val viewModel = bindViewModel<Int, EntryViewModel>(id)
    val entry by viewModel.entry.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEntry()
    }

    entry?.let {
        val document = Jsoup.parse(it.content)
        document.select(whitelistRules.keys.joinToString(separator = "") { ":not(${it})" }).remove()

        whitelistRules.forEach { (tagName, attributeNames) ->
            document.select(tagName)
                .forEach { element ->
                    element.attributes()
                        .map { it.key }
                        .filter { !attributeNames.contains(it) }
                        .forEach { element.removeAttr(it) }
                }
        }

        replacementRules.forEach { (tagName, replacementRules) ->
            document.select(tagName)
                .forEach { element ->
                    replacementRules.forEach { rule ->
                        element.attr(rule.attributeName, rule.attributeValue)
                    }
                }
        }

        Column(Modifier.verticalScroll(rememberScrollState())) {
            Text(
                it.title,
                fontWeight = FontWeight.Bold
            )

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        clipToOutline = true
                        loadData(document.html(), "text/html", "UTF-8")
                    }
                }
            )
        }
    } ?: CircularProgressIndicator()
}