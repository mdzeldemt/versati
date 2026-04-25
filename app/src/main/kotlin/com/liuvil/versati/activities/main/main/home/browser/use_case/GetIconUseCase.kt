package com.liuvil.versati.activities.main.main.home.browser.use_case

import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.repository.data.Icon
import javax.inject.Inject

internal class GetIconUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        id: Int
    ): Result<Icon> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.getIconById(
                id = id
            )
        }
    }
}