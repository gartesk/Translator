package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text

sealed class TranslationViewState(
    val textFrom: Text,
    val languageTo: Language
)

class EmptyTranslationViewState(
    textFrom: Text,
    languageTo: Language
) : TranslationViewState(textFrom, languageTo)

class LoadingTranslationViewState(
    textFrom: Text,
    languageTo: Language
) : TranslationViewState(textFrom, languageTo)

class ResultTranslationViewState(
    textFrom: Text,
    languageTo: Language,
    val result: String
) : TranslationViewState(textFrom, languageTo)

class ErrorTranslationViewState(
    textFrom: Text,
    languageTo: Language,
    val error: ErrorType
): TranslationViewState(textFrom, languageTo) {
    enum class ErrorType {
        CONNECTION, EMPTY_TEXT, TARGET_LANGUAGE
    }
}
