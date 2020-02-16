package com.gartesk.translator

import android.app.Application
import com.gartesk.translator.data.stats.ObjectBoxStatsRepository
import com.gartesk.translator.data.translation.SystranTranslationRepository
import com.gartesk.translator.di.CommandFactory

class TranslatorApplication : Application() {

	lateinit var commandFactory: CommandFactory

	override fun onCreate() {
		super.onCreate()
		val translationRepository =
			SystranTranslationRepository(
				this
			)
		val statsRepository =
			ObjectBoxStatsRepository(this)
		commandFactory = CommandFactory(translationRepository, statsRepository)
	}
}