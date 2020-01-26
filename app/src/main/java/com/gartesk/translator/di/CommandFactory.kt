package com.gartesk.translator.di

import com.gartesk.translator.domain.command.GetDirectionsCommand
import com.gartesk.translator.domain.command.GetTranslationCommand
import com.gartesk.translator.domain.repository.CounterRepository
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
    private val translationRepository: TranslationRepository,
    private val counterRepository: CounterRepository
) {

    fun createGetTranslationCommand(): GetTranslationCommand =
        GetTranslationCommand(translationRepository, counterRepository)

    fun createGetDirectionsCommand(): GetDirectionsCommand =
        GetDirectionsCommand(translationRepository)
}