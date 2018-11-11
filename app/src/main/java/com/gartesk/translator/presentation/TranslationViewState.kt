package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text

interface TranslationViewState {
    fun applyTo(viewState: FullTranslationViewState): FullTranslationViewState
}

sealed class FullTranslationViewState(
    val textFrom: Text,
    val textTo: Text,
    val languages: List<Language>
) : TranslationViewState {

    override fun applyTo(viewState: FullTranslationViewState): FullTranslationViewState =
        when (viewState) {
            is IdleTranslationViewState -> IdleTranslationViewState(
                textFrom,
                textTo,
                languages
            )
            is LoadingTranslationViewState -> LoadingTranslationViewState(
                textFrom,
                textTo,
                languages
            )
            is ErrorTranslationViewState -> ErrorTranslationViewState(
                textFrom,
                textTo,
                viewState.error,
                languages
            )
        }
}

class IdleTranslationViewState(
    textFrom: Text,
    textTo: Text,
    languages: List<Language> = emptyList()
) : FullTranslationViewState(textFrom, textTo, languages)

class LoadingTranslationViewState(
    textFrom: Text,
    textTo: Text,
    languages: List<Language> = emptyList()
) : FullTranslationViewState(textFrom, textTo, languages)

class ErrorTranslationViewState(
    textFrom: Text,
    textTo: Text,
    val error: ErrorType,
    languages: List<Language> = emptyList()
) : FullTranslationViewState(textFrom, textTo, languages) {
    enum class ErrorType {
        CONNECTION, EMPTY_TEXT, TARGET_LANGUAGE
    }
}

sealed class PartialTranslationViewState : TranslationViewState

class LoadedLanguagesTranslationViewState(
    val languages: List<Language>
) : PartialTranslationViewState() {

    override fun applyTo(viewState: FullTranslationViewState): FullTranslationViewState =
        when (viewState) {
            is IdleTranslationViewState -> IdleTranslationViewState(
                viewState.textFrom,
                viewState.textTo,
                languages
            )
            is LoadingTranslationViewState -> LoadingTranslationViewState(
                viewState.textFrom,
                viewState.textTo,
                languages
            )
            is ErrorTranslationViewState -> ErrorTranslationViewState(
                viewState.textFrom,
                viewState.textTo,
                viewState.error,
                languages
            )
        }
}

class SelectedLanguageFromTranslationViewState(
    val language: Language
) : PartialTranslationViewState() {

    override fun applyTo(viewState: FullTranslationViewState): FullTranslationViewState =
        when (viewState) {
            is IdleTranslationViewState -> IdleTranslationViewState(
                viewState.textFrom.copy(language = language),
                viewState.textTo,
                viewState.languages
            )
            is LoadingTranslationViewState -> LoadingTranslationViewState(
                viewState.textFrom.copy(language = language),
                viewState.textTo,
                viewState.languages
            )
            is ErrorTranslationViewState -> ErrorTranslationViewState(
                viewState.textFrom.copy(language = language),
                viewState.textTo,
                viewState.error,
                viewState.languages
            )
        }
}

class SelectedLanguageToTranslationViewState(
    val language: Language
) : PartialTranslationViewState() {

    override fun applyTo(viewState: FullTranslationViewState): FullTranslationViewState =
        when (viewState) {
            is IdleTranslationViewState -> IdleTranslationViewState(
                viewState.textFrom,
                viewState.textTo.copy(language = language),
                viewState.languages
            )
            is LoadingTranslationViewState -> LoadingTranslationViewState(
                viewState.textFrom,
                viewState.textTo.copy(language = language),
                viewState.languages
            )
            is ErrorTranslationViewState -> ErrorTranslationViewState(
                viewState.textFrom,
                viewState.textTo.copy(language = language),
                viewState.error,
                viewState.languages
            )
        }
}

class ChangedTextTranslationViewState(
    val text: String
) : PartialTranslationViewState() {

    override fun applyTo(viewState: FullTranslationViewState): FullTranslationViewState =
        when (viewState) {
            is IdleTranslationViewState -> IdleTranslationViewState(
                viewState.textFrom.copy(content = text),
                viewState.textTo,
                viewState.languages
            )
            is LoadingTranslationViewState -> LoadingTranslationViewState(
                viewState.textFrom.copy(content = text),
                viewState.textTo,
                viewState.languages
            )
            is ErrorTranslationViewState -> ErrorTranslationViewState(
                viewState.textFrom.copy(content = text),
                viewState.textTo,
                viewState.error,
                viewState.languages
            )
        }
}