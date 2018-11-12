package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text

data class PartialState(
    val textFrom: Text = Text(),
    val languageTo: Language = Language.UNKNOWN_LANGUAGE,
    val languages: List<Language> = emptyList()
) {

    fun apply(visitor: PartialStateVisitor): PartialState =
        visitor.visit(this)
}

interface PartialStateVisitor {

    fun visit(partialState: PartialState): PartialState
}

data class LoadedLanguagesPartialStateVisitor(val languages: List<Language>) : PartialStateVisitor {

    override fun visit(partialState: PartialState): PartialState =
        partialState.copy(languages = languages)
}

data class SelectedLanguageFromPartialStateVisitor(val language: Language) : PartialStateVisitor {

    override fun visit(partialState: PartialState): PartialState =
        partialState.copy(textFrom = partialState.textFrom.copy(language = language))
}

data class SelectedLanguageToPartialStateVisitor(val language: Language) : PartialStateVisitor {

    override fun visit(partialState: PartialState): PartialState =
        partialState.copy(languageTo = language)
}

data class ChangedTextPartialStateVisitor(val text: String) : PartialStateVisitor {

    override fun visit(partialState: PartialState): PartialState =
        partialState.copy(textFrom = partialState.textFrom.copy(content = text))
}