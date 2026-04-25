package com.mdzeldemt.versati.activities.main.preferences.connection.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.activities.main.preferences.connection.Details
import javax.inject.Inject

internal class GetDetailsUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(): Result<Details> {
        val repository = repositoryFactory.create()

        return runCatching {
            repository.getVersion().let {
                Details(
                    version = it.version,
                    commit = it.commit,
                    buildDate = it.buildDate,
                    goVersion = it.goVersion,
                    compiler = it.compiler,
                    arch = it.arch,
                    os = it.os,
                )
            }
        }
    }
}