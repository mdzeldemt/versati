package com.liuvil.versati.activities.main.main.home.category.remove

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

internal data class InitData(
    val categoryId: Int
)

@HiltViewModel
internal class RemoveCategoryDialogModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<InitData>() {

    private var categoryId: Int = -1

    private lateinit var repository: Repository

    var title: String = ""
        private set

    override suspend fun initialize(initData: InitData) {
        categoryId = initData.categoryId
        repository = repositoryFactory.create()
    }

    suspend fun loadCategoryTitle() {
        title = repository.getAllCategories(
            origin = Origin.LocalThenRemote
        ).first {
            it.id == categoryId
        }
        .title
    }

    suspend fun deleteCategory() {
        repository.deleteCategory(
            id = categoryId
        )
    }
}