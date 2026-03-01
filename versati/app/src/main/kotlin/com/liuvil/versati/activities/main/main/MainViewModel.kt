package com.liuvil.versati.activities.main.main

import androidx.lifecycle.viewModelScope
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.preferences.PreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

internal sealed class OnboardingState {
    data object Loading: OnboardingState()
    data object Incomplete: OnboardingState()
    data object Complete: OnboardingState()
}

@HiltViewModel
internal class MainViewModel @Inject constructor(
    preferenceStore: PreferenceStore
): BaseViewModel<Unit>() {
    val onboardingState =
        combine(
            preferenceStore.baseUrl,
            preferenceStore.credentials
        ) { baseUrl, credentials ->
            if (baseUrl != null && credentials != null) {
                OnboardingState.Complete
            } else {
                OnboardingState.Incomplete
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = OnboardingState.Loading
        )
}
