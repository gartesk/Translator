package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text

sealed class TranslationViewState(
    val textFrom: Text,
    val languageTo: Language,
    val languages: List<Language>
)

class EmptyTranslationViewState(
    textFrom: Text,
    languageTo: Language,
    languages: List<Language> = emptyList()
) : TranslationViewState(textFrom, languageTo, languages)

class LoadingTranslationViewState(
    textFrom: Text,
    languageTo: Language,
    languages: List<Language> = emptyList()
) : TranslationViewState(textFrom, languageTo, languages)

class ResultTranslationViewState(
    textFrom: Text,
    languageTo: Language,
    val result: String,
    languages: List<Language> = emptyList()
) : TranslationViewState(textFrom, languageTo, languages)

class ErrorTranslationViewState(
    textFrom: Text,
    languageTo: Language,
    val error: ErrorType,
    languages: List<Language> = emptyList()
) : TranslationViewState(textFrom, languageTo, languages) {
    enum class ErrorType {
        CONNECTION, EMPTY_TEXT, TARGET_LANGUAGE
    }
}

class LanguagesLoadedTranslationViewState(
    languages: List<Language>
) : TranslationViewState(Text(""), Language.UNKNOWN_LANGUAGE, languages)

fun TranslationViewState.copyWithLanguages(languages: List<Language>): TranslationViewState =
    when (this) {
        is EmptyTranslationViewState -> EmptyTranslationViewState(textFrom, languageTo, languages)
        is LoadingTranslationViewState -> LoadingTranslationViewState(
            textFrom,
            languageTo,
            languages
        )
        is ResultTranslationViewState -> ResultTranslationViewState(
            textFrom,
            languageTo,
            result,
            languages
        )
        is ErrorTranslationViewState -> ErrorTranslationViewState(
            textFrom,
            languageTo,
            error,
            languages
        )
        is LanguagesLoadedTranslationViewState -> LanguagesLoadedTranslationViewState(languages)
    }