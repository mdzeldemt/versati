package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import javax.inject.Inject

internal class MarkEntriesAsReadUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        entryIds: List<Int>
    ): Result<Unit> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.updateEntriesRead(
                ids = entryIds,
                read = true
            )
        }
    }
}