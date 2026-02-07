package com.liuvil.versati.activities.main.main.home.category.edit

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
internal class EditCategoryDialogModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<InitData>() {

    private var categoryId: Int = -1

    private lateinit var repository: Repository

    val title = mutableStateOf("")

    override suspend fun initialize(initData: InitData) {
        categoryId = initData.categoryId
        repository = repositoryFactory.create()
    }

    suspend fun getCategoryTitle() =
        repository.getAllCategories(
            origin = Origin.LocalThenRemote
        ).first {
            it.id == categoryId
        }
        .title

    suspend fun updateCategory() {
        repository.updateCategory(
            id = categoryId,
            title = title.value
        )
    }
}