package com.liuvil.versati.activities.main.main.home.browser.use_case

import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import javax.inject.Inject

internal class RemoveCategoryUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend operator fun invoke(
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