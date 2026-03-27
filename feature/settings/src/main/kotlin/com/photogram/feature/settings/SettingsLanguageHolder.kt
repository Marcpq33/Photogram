package com.photogram.feature.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory singleton that holds the currently selected language code across
 * SettingsViewModel and LanguagePickerViewModel within the same process.
 * Code values match AuthStrings: EN, ES, FR, IT, ZH, JA.
 */
@Singleton
class SettingsLanguageHolder @Inject constructor() {

    private val _code = MutableStateFlow("EN")
    val code: StateFlow<String> = _code.asStateFlow()

    fun set(code: String) {
        _code.value = code
    }
}
