package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.repository.data.Feed
import javax.inject.Inject

internal class RefreshFeedUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        id: Int
    ): Result<Unit> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.refreshFeed(id)
        }
    }
}