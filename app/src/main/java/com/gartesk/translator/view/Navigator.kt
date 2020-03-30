package com.gartesk.translator.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavArgument
import androidx.navigation.fragment.findNavController
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import kotlinx.android.synthetic.main.activity_main.*

class Navigator(private val activity: MainActivity) {

	companion object {
		private const val KEY_TEXT = "translation.text"
		private const val KEY_LANGUAGE_FROM = "translation.languageFrom"
		private const val KEY_LANGUAGE_TO = "translation.languageTo"
	}

	private val navController = activity.navHostFragment.findNavController()
	private val navigationView = activity.navigationView

	init {
		initNavGraph()
		initNavigationView()
	}

	private fun initNavGraph() {
		val intent = activity.intent
		val navInflater = navController.navInflater
		val graph = navInflater.inflate(R.navigation.nav_graph)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val textToProcess = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
			val textToProcessArgument = NavArgument.Builder()
				.setDefaultValue(textToProcess)
				.setIsNullable(true)
				.build()
			graph.addArgument(KEY_TEXT, textToProcessArgument)
		}
		navController.graph = graph
	}

	private fun initNavigationView() {
		navigationView.setOnNavigationItemSelectedListener {
			val currentId = navController.currentDestination?.id
			when {
				it.itemId == R.id.translation && currentId != R.id.translationFragment ->
					navController.navigate(R.id.actionToTranslationFragment)

				it.itemId == R.id.stats && currentId != R.id.statsFragment ->
					navController.navigate(R.id.actionToStatsFragment)
			}
			true
		}
	}

	fun onNewIntent(intent: Intent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val textToProcess: CharSequence? = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
			val bundle = bundleOf(KEY_TEXT to textToProcess)
			openTranslationInternal(bundle)
		}
	}

	fun openTranslation(text: Text, languageTo: Language) {
		val bundle = bundleOf(
			KEY_TEXT to text.content,
			KEY_LANGUAGE_FROM to text.language.code,
			KEY_LANGUAGE_TO to languageTo.code
		)
		openTranslationInternal(bundle)
	}

	private fun openTranslationInternal(bundle: Bundle) {
		navController.navigate(R.id.actionToTranslationFragment, bundle)
		activity.navigationView.selectedItemId = R.id.translation
	}

	fun getArguments(bundle: Bundle?): Pair<Text, Language>? {
		val textContent = bundle?.getString(KEY_TEXT)
		val languageFromCode = bundle?.getString(KEY_LANGUAGE_FROM)
		val languageToCode = bundle?.getString(KEY_LANGUAGE_TO)
		return if (textContent != null) {
			val languageFrom = if (languageFromCode != null) Language(languageFromCode) else Language.UNKNOWN_LANGUAGE
			val languageTo = if (languageToCode != null) Language(languageToCode) else Language.UNKNOWN_LANGUAGE
			Text(content = textContent, language = languageFrom) to languageTo
		} else {
			null
		}
	}
}