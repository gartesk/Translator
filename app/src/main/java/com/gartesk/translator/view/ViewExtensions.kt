package com.gartesk.translator.view

import android.view.View
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.di.CommandFactory

val View.commandFactory: CommandFactory
	get() = (context.applicationContext as TranslatorApplication).commandFactory