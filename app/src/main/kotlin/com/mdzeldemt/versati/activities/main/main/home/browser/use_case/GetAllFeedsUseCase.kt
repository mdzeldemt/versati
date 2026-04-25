package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.repository.data.Feed
import javax.inject.Inject

internal class GetAllFeedsUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(): Result<List<Feed>> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.getAllFeeds()
        }
    }
}