package com.liuvil.versati.activities.main.main.home.feed.edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject

internal data class InitData(
    val feedId: Int
)

internal data class Category(
    val id: Int,
    val title: String
)

@HiltViewModel
internal class EditFeedDialogModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<InitData>() {

    private var feedId = -1

    private lateinit var repository: Repository

    private val _categories = mutableStateOf<List<Category>>(listOf())

    val categories: State<List<Category>> = _categories

    val title = mutableStateOf("")
    val feedUrl = mutableStateOf("")
    val categoryId = mutableIntStateOf(-1)

    override suspend fun initialize(initData: InitData) {
        feedId = initData.feedId
        repository = repositoryFactory.create()
    }

    suspend fun loadFeedDetails() {
        val feed = repository.getFeedById(
            id = feedId,
            origin = Origin.LocalThenRemote
        )

        title.value = feed.title
        feedUrl.value = feed.feedUrl.toString()
        categoryId.intValue = feed.categoryId
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

    suspend fun updateFeed() {
        repository.updateFeed(
            id = feedId,
            title = title.value,
            feedUrl = feedUrl.value.toHttpUrl().toUrl(),
            categoryId = categoryId.intValue
        )
    }
}