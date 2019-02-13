package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Text

sealed class TranslationViewState(
    val textFrom: Text,
    val textTo: Text
)

class IdleTranslationViewState(
    textFrom: Text = Text(),
    textTo: Text = Text(),
    val counter: Int? = null
) : TranslationViewState(textFrom, textTo)

class LoadingTranslationViewState(
    textFrom: Text,
    textTo: Text
) : TranslationViewState(textFrom, textTo)

class ErrorTranslationViewState(
    textFrom: Text,
    textTo: Text,
    val error: ErrorType
) : TranslationViewState(textFrom, textTo) {
    enum class ErrorType {
        CONNECTION, EMPTY_TEXT, TARGET_LANGUAGE
    }
}