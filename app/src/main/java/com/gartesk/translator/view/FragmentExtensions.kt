package com.gartesk.translator.view

import androidx.fragment.app.Fragment
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.di.CommandFactory

fun Fragment.getCommandFactory(): CommandFactory =
	(requireActivity().application as TranslatorApplication).commandFactory