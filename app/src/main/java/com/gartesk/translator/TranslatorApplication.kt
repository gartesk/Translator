package com.gartesk.translator

import android.app.Application
import com.gartesk.translator.data.MockTranslationRepository
import com.gartesk.translator.data.YandexTranslationRepository
import com.gartesk.translator.di.CommandFactory

class TranslatorApplication : Application() {

    lateinit var commandFactory: CommandFactory

    override fun onCreate() {
        super.onCreate()
        val translationRepository = YandexTranslationRepository()
        commandFactory = CommandFactory(translationRepository)
    }
}