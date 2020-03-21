package com.gartesk.translator.view

import androidx.fragment.app.Fragment
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.di.CommandFactory

val Fragment.commandFactory: CommandFactory
	get() = (requireActivity().application as TranslatorApplication).commandFactory

val Fragment.navigator: Navigator
	get() = (requireActivity() as MainActivity).navigator