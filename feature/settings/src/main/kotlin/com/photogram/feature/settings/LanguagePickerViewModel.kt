package com.photogram.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguagePickerViewModel @Inject constructor(
    private val languageHolder: SettingsLanguageHolder,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LanguagePickerUiState(selectedCode = languageHolder.code.value),
    )
    val uiState: StateFlow<LanguagePickerUiState> = _uiState.asStateFlow()

    fun onAction(action: LanguagePickerUiAction) {
        when (action) {
            is LanguagePickerUiAction.LanguageSelected -> {
                _uiState.update { it.copy(selectedCode = action.code) }
                languageHolder.set(action.code)
                viewModelScope.launch {
                    userPreferencesRepository.setLanguage(action.code)
                }
            }
        }
    }
}
