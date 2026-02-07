package com.liuvil.versati.activities.main.main.home.feed.add

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.lazyLoad
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject

internal data class Category(
    val id: Int,
    val title: String
)

@HiltViewModel
internal class AddFeedDialogModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<Unit>() {
    private lateinit var repository: Repository

    private val _categories = mutableStateOf<List<Category>>(listOf())

    val categories: State<List<Category>> = _categories

    val feedUrl = mutableStateOf("")
    val categoryId = mutableStateOf<Int?>(null)

    override suspend fun initialize(initData: Unit) {
        repository = repositoryFactory.create()
    }

    suspend fun loadCategories() {
        _categories.value = repository.getAllCategories(
            origin = Origin.LocalThenRemote
        ).map {
            Category(
                id = it.id,
                title = it.title
            )
        }
    }

    suspend fun createFeed() {
        repository.createFeed(
            feedUrl = feedUrl.value.toHttpUrl().toUrl(),
            categoryId = categoryId.value!!
        )
    }
}