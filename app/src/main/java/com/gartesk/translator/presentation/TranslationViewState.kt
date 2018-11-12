package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text

sealed class TranslationViewState(
    val textFrom: Text,
    val textTo: Text,
    val languages: List<Language>
)

class IdleTranslationViewState(
    textFrom: Text = Text(),
    textTo: Text = Text(),
    languages: List<Language> = emptyList()
) : TranslationViewState(textFrom, textTo, languages)

class LoadingTranslationViewState(
    textFrom: Text,
    textTo: Text,
    languages: List<Language> = emptyList()
) : TranslationViewState(textFrom, textTo, languages)

class ErrorTranslationViewState(
    textFrom: Text,
    textTo: Text,
    val error: ErrorType,
    languages: List<Language> = emptyList()
) : TranslationViewState(textFrom, textTo, languages) {
    enum class ErrorType {
        CONNECTION, EMPTY_TEXT, TARGET_LANGUAGE
    }
}