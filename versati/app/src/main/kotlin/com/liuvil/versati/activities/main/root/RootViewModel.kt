package com.liuvil.versati.activities.main.root

import androidx.lifecycle.viewModelScope
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.preferences.PreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    preferenceStore: PreferenceStore
): BaseViewModel<Unit>() {

    val activeServerID = preferenceStore.activeServerID
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )
}
