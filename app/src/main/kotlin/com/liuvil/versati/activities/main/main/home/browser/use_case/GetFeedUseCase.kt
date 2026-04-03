package com.liuvil.versati.activities.main.main.home.browser.use_case

import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.repository.data.Feed
import javax.inject.Inject

internal class GetFeedUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend operator fun invoke(
        id: Int
    ): Result<Feed> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.getFeedById(id)
        }
    }
}