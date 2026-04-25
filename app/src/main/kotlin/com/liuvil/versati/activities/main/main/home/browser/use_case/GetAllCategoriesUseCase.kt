package com.liuvil.versati.activities.main.main.home.browser.use_case

import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.repository.data.Category
import javax.inject.Inject

internal class GetAllCategoriesUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(): Result<List<Category>> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.getAllCategories()
        }
    }
}