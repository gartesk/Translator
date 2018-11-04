package com.gartesk.translator.di

import com.gartesk.translator.domain.command.ListLanguagesCommand
import com.gartesk.translator.domain.command.SingleCommand
import com.gartesk.translator.domain.command.TranslateTextToLanguageCommand
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
    private val translationRepository: TranslationRepository
) {

    fun createTranslateTextToLanguageCommand(): SingleCommand<Pair<Text, Language>, Translation> =
        TranslateTextToLanguageCommand(translationRepository)

    fun createListLanguagesCommand(): SingleCommand<Unit, List<Language>> =
        ListLanguagesCommand(translationRepository)
}