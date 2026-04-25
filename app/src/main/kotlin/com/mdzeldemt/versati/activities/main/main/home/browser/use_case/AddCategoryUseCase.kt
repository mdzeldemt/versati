package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.repository.data.Category
import javax.inject.Inject

internal class AddCategoryUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        title: String
    ): Result<Int> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.createCategory(
                title = title
            ).id
        }
    }
}