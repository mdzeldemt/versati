package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.repository.data.Feed
import java.net.URL
import javax.inject.Inject

internal class AddFeedUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        feedUrl: URL,
        categoryId: Int
    ): Result<Int> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.createFeed(
                feedUrl = feedUrl,
                categoryId = categoryId
            )
        }
    }
}