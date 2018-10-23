package com.gartesk.translator.di

import com.gartesk.translator.domain.command.MaybeCommand
import com.gartesk.translator.domain.command.TranslateStringCommand
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
    private val translationRepository: TranslationRepository
) {

    fun createTranslateStringCommand(): MaybeCommand<String, String> =
        TranslateStringCommand(translationRepository)
}