package com.gartesk.translator.di

import com.gartesk.translator.domain.command.MaybeCommand
import com.gartesk.translator.domain.command.TranslateTextToLanguageCommand
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
    private val translationRepository: TranslationRepository
) {

    fun createTranslateStringCommand(): MaybeCommand<Pair<Text, Language>, Translation> =
        TranslateTextToLanguageCommand(translationRepository)
}