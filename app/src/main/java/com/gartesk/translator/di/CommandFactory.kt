package com.gartesk.translator.di

import com.gartesk.translator.domain.command.GetDefaultLanguageCommand
import com.gartesk.translator.domain.command.GetLanguagesCommand
import com.gartesk.translator.domain.command.GetTranslationCommand
import com.gartesk.translator.domain.command.ObserveAllStatsCommand
import com.gartesk.translator.domain.repository.DeviceRepository
import com.gartesk.translator.domain.repository.StatsRepository
import com.gartesk.translator.domain.repository.TranslationRepository

class CommandFactory(
	private val translationRepository: TranslationRepository,
	private val statsRepository: StatsRepository,
	private val deviceRepository: DeviceRepository
) {

	fun createGetTranslationCommand(): GetTranslationCommand =
		GetTranslationCommand(translationRepository, statsRepository)

	fun createGetDirectionsCommand(): GetLanguagesCommand =
		GetLanguagesCommand(translationRepository)

	fun createObserveAllStatsCommand(): ObserveAllStatsCommand =
		ObserveAllStatsCommand(statsRepository)

	fun createGetDefaultLanguageCommand(): GetDefaultLanguageCommand =
		GetDefaultLanguageCommand(statsRepository, deviceRepository)
}