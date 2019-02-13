package com.gartesk.translator.di

import com.gartesk.translator.domain.command.*
import com.gartesk.translator.domain.entity.*
import com.gartesk.translator.domain.repository.CounterRepository
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
    private val translationRepository: TranslationRepository,
    private val counterRepository: CounterRepository
) {

    fun createGetTranslationCommand(): SingleCommand<Pair<Text, Language>, Translation> =
        GetTranslationCommand(translationRepository, counterRepository)

    fun createGetDirectionsCommand(): SingleCommand<Unit, List<Direction>> =
        GetDirectionsCommand(translationRepository)
}