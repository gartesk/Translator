package com.gartesk.translator

import android.app.Application
import com.gartesk.translator.data.PrefsCounterRepository
import com.gartesk.translator.data.SystranTranslationRepository
import com.gartesk.translator.di.CommandFactory

class TranslatorApplication : Application() {

    lateinit var commandFactory: CommandFactory

    override fun onCreate() {
        super.onCreate()
        val translationRepository = SystranTranslationRepository()
        val counterRepository = PrefsCounterRepository(this)
        commandFactory = CommandFactory(translationRepository, counterRepository)
    }
}