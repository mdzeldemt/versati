package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.repository.data.Category
import javax.inject.Inject

internal class EditCategoryUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
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