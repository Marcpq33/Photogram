package com.photogram.feature.settings

sealed interface LanguagePickerUiAction {
    data class LanguageSelected(val code: String) : LanguagePickerUiAction
}
