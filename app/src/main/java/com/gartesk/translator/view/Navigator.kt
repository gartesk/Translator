package com.gartesk.translator.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text

class Navigator(private val context: Context) {

	companion object {
		private const val KEY_TEXT = "translation.text"
		private const val KEY_LANGUAGE_FROM = "translation.languageFrom"
		private const val KEY_LANGUAGE_TO = "translation.languageTo"
	}

	fun getArguments(bundle: Bundle?): Pair<Text, Language>? {
		val text = bundle?.getString(KEY_TEXT)
		val languageFrom = bundle?.getString(KEY_LANGUAGE_FROM)
		val languageTo = bundle?.getString(KEY_LANGUAGE_TO)
		return if (text != null && languageFrom != null && languageTo != null) {
			Text(content = text, language = Language(code = languageFrom)) to Language(code = languageTo)
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				val textToProcess = bundle?.getCharSequence(Intent.EXTRA_PROCESS_TEXT)
				if (textToProcess != null) {
					Text(content = textToProcess.toString(), language = Language.UNKNOWN_LANGUAGE) to Language.UNKNOWN_LANGUAGE
				} else {
					null
				}
			} else {
				null
			}
		}
	}

	fun openTranslation(text: Text, languageTo: Language) {
		val intent = Intent(context, MainActivity::class.java).apply {
			putExtra(KEY_TEXT, text.content)
			putExtra(KEY_LANGUAGE_FROM, text.language.code)
			putExtra(KEY_LANGUAGE_TO, languageTo.code)
		}
		context.startActivity(intent)
	}
}