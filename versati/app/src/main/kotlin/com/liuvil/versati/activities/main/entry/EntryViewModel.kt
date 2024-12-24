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
    val enclosureId: Int?
)

data class Enclosure(
    val url: URL
)

enum class Status {
    UNINITIALIZED,
    LOADING,
    IDLE
}

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseViewModel<Int>() {

    private val _status = MutableStateFlow(Status.UNINITIALIZED)
    private var _entryId: Int? = null
    private val _entry = MutableStateFlow<Entry?>(null)
    private val _enclosure = MutableStateFlow<Enclosure?>(null)

    val status: StateFlow<Status> = _status
    val entry: StateFlow<Entry?> = _entry
    val enclosure: StateFlow<Enclosure?> = _enclosure

    override suspend fun initialize(initData: Int) {
        _entryId = initData
    }

    suspend fun loadAll() {
        _status.value = Status.LOADING

        loadEntry()

        _entry.value?.enclosureId?.let {
            loadEnclosure(id = it)
        }

        _status.value = Status.IDLE
    }

    private suspend fun loadEntry() {
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
                    enclosureId = entry.enclosures.firstOrNull()?.id
                )
            }
        }
    }

    private suspend fun loadEnclosure(id: Int) {
        _enclosure.value = Enclosure(url = minifluxApi.getEnclosure(id).url)
    }

}
