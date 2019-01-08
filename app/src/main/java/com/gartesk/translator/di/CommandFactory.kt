package com.gartesk.translator.di

import com.gartesk.translator.domain.command.*
import com.gartesk.translator.domain.entity.Counter
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.CounterRepository
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
    private val translationRepository: TranslationRepository,
    private val counterRepository: CounterRepository
) {

    fun createTranslateTextToLanguageCommand(): SingleCommand<Pair<Text, Language>, Translation> =
        TranslateTextToLanguageCommand(translationRepository, counterRepository)

    fun createListLanguagesCommand(): ObservableCommand<Unit, List<Language>> =
        ListLanguagesCommand(translationRepository)

    fun createGetCounterUpdatesCommand(): ObservableCommand<Text, Counter> =
        GetCounterUpdatesCommand(counterRepository)
}