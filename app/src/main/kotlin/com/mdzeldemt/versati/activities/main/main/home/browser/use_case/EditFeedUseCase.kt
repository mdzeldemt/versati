package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.repository.data.Feed
import java.net.URL
import javax.inject.Inject

internal class EditFeedUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        id: Int,
        title: String,
        feedUrl: URL,
        categoryId: Int
    ): Result<Feed> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.updateFeed(
                id = id,
                title = title,
                feedUrl = feedUrl,
                categoryId = categoryId
            )
        }
    }
}