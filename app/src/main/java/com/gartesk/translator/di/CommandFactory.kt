package com.gartesk.translator.di

import com.gartesk.translator.domain.command.GetDirectionsCommand
import com.gartesk.translator.domain.command.GetTranslationCommand
import com.gartesk.translator.domain.repository.StatsRepository
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
	private val translationRepository: TranslationRepository,
	private val statsRepository: StatsRepository
) {

	fun createGetTranslationCommand(): GetTranslationCommand =
		GetTranslationCommand(translationRepository, statsRepository)

	fun createGetDirectionsCommand(): GetDirectionsCommand =
		GetDirectionsCommand(translationRepository)
}