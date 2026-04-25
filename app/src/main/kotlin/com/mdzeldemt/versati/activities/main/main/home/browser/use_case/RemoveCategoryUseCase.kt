package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import javax.inject.Inject

internal class RemoveCategoryUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        id: Int
    ): Result<Unit> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.deleteCategory(
                id = id
            )
        }
    }
}