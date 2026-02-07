package com.liuvil.versati.activities.main.main.home.category.add

import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

internal data class Category(
    val id: Int,
    val title: String
)

@HiltViewModel
internal class AddCategoryDialogModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<Unit>() {
    private lateinit var repository: Repository

    val title = mutableStateOf("")

    override suspend fun initialize(initData: Unit) {
        repository = repositoryFactory.create()
    }

    suspend fun createCategory() {
        repository.createCategory(
            title = title.value
        )
    }
}