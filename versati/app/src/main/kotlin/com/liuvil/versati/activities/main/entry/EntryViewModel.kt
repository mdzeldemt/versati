package com.liuvil.versati.activities.main.entry

import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.framework.html.AttributeRewriteRule
import com.liuvil.versati.framework.html.AttributeWhitelistRule
import com.liuvil.versati.framework.html.ElementWhitelistRule
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import javax.inject.Inject

// TODO: Load from user settings
private val rewriteRules = listOf(
    ElementWhitelistRule(listOf("html", "body", "p", "span", "a", "img")),
    AttributeWhitelistRule("html, body, p, span", emptySet()),
    AttributeWhitelistRule("a", setOf("href")),
    AttributeWhitelistRule("img", setOf("src")),
    AttributeRewriteRule("img", "style", "max-width: 100%;")
)

data class Entry(
    val title: String,
    val content: Document,
    val url: URL,
    val enclosures: List<Enclosure>
)

data class Enclosure(
    val url: URL
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseViewModel<Int>() {

    private var _entryId: Int? = null
    private val _entry = MutableStateFlow<Entry?>(null)

    val entry: StateFlow<Entry?> = _entry

    override suspend fun initialize(initData: Int) {
        _entryId = initData
    }

    suspend fun loadEntry() {
        _entryId?.let { entryId ->
            _entry.value = minifluxApi.getEntry(entryId).let { entry ->
                val content = Jsoup.parse(entry.content)
                rewriteRules.forEach { rule ->
                    rule.apply(content)
                }

                Entry(
                    title = entry.title,
                    content = content,
                    url = entry.url,
                    enclosures = entry.enclosures
                        .map { enclosure ->
                            Enclosure(url = enclosure.url)
                        }
                )
            }
        }
    }

}
