package com.liuvil.versati.activities.main.main.home.browser.use_case

import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.repository.data.Category
import javax.inject.Inject

internal class EditCategoryUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend operator fun invoke(
        id: Int,
        title: String
    ): Result<Category> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.updateCategory(
                id = id,
                title = title
            )
        }
    }
}