package com.photogram.feature.settings

data class LanguagePickerUiState(
    val selectedCode: String = "EN",
    val languages: List<LanguageOption> = LanguageDefaults.all,
)

data class LanguageOption(val code: String, val name: String, val flag: String)

object LanguageDefaults {
    /** Exact same set as AuthStrings: EN, ES, FR, IT, ZH, JA (uppercase codes). */
    val all = listOf(
        LanguageOption("EN", "English",  "🇬🇧"),
        LanguageOption("ES", "Español",  "🇪🇸"),
        LanguageOption("FR", "Français", "🇫🇷"),
        LanguageOption("IT", "Italiano", "🇮🇹"),
        LanguageOption("ZH", "中文",     "🇨🇳"),
        LanguageOption("JA", "日本語",   "🇯🇵"),
    )
}
