package com.gartesk.translator

import android.app.Application
import com.gartesk.translator.data.device.AndroidDeviceRepository
import com.gartesk.translator.data.stats.ObjectBoxStatsRepository
import com.gartesk.translator.data.translation.GoogleTranslationRepository
import com.gartesk.translator.di.CommandFactory

class TranslatorApplication : Application() {

	lateinit var commandFactory: CommandFactory

	override fun onCreate() {
		super.onCreate()
		val translationRepository = GoogleTranslationRepository()
		val statsRepository = ObjectBoxStatsRepository(this)
		val deviceRepository = AndroidDeviceRepository()
		commandFactory = CommandFactory(translationRepository, statsRepository, deviceRepository)
	}
}