package com.liuvil.versati.activities.main.entry

import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import javax.inject.Inject

data class Entry(
    val title: String,
    val content: String
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
            _entry.value = minifluxApi.getEntry(entryId).let {
                Entry(
                    title = it.title,
                    content = it.content
                )
            }
        }
    }

}
