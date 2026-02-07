package com.liuvil.versati.activities.main.main.home.feed.remove

import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

internal data class InitData(
    val feedId: Int
)

@HiltViewModel
internal class RemoveFeedDialogModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<InitData>() {

    private var feedId: Int = -1

    private lateinit var repository: Repository

    var title: String = ""
        private set

    override suspend fun initialize(initData: InitData) {
        feedId = initData.feedId
        repository = repositoryFactory.create()
    }

    suspend fun loadFeedTitle() {
        title = repository.getFeedById(
            id = feedId,
            origin = Origin.LocalThenRemote
        ).title
    }

    suspend fun deleteFeed() {
        repository.deleteFeed(
            id = feedId
        )
    }
}