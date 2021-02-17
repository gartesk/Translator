package com.gartesk.translator.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavArgument
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text

class Navigator(private val activity: MainActivity) {

	companion object {
		private const val KEY_TEXT = "translation.text"
		private const val KEY_LANGUAGE_FROM = "translation.languageFrom"
		private const val KEY_LANGUAGE_TO = "translation.languageTo"
	}

	private val navController = activity.supportFragmentManager
		.findFragmentById(R.id.navHostFragment)?.findNavController()
		?: throw IllegalStateException("No nav controller found")
	private val navigationView = activity.binding.navigationView
	private val rootDestinationIds = setOf(R.id.translation, R.id.stats)

	init {
		initNavGraph()
		initViews()
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

	private fun initViews() {
		navigationView.setupWithNavController(navController)

		val appBarConfiguration = AppBarConfiguration(rootDestinationIds)
		activity.setupActionBarWithNavController(navController, appBarConfiguration)

		navController.addOnDestinationChangedListener { _, destination, _ ->
			navigationView.visibility = when (destination.id) {
				in rootDestinationIds -> View.VISIBLE
				else -> View.GONE
			}
		}
	}

	fun onNewIntent(intent: Intent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val textToProcess: CharSequence? =
				intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
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
		navController.navigate(R.id.translation, bundle)
	}

	fun getTranslationArguments(bundle: Bundle?): Pair<Text, Language>? {
		val textContent = bundle?.getString(KEY_TEXT)
		val languageFromCode = bundle?.getString(KEY_LANGUAGE_FROM)
		val languageToCode = bundle?.getString(KEY_LANGUAGE_TO)
		return if (textContent != null) {
			val languageFrom =
				if (languageFromCode != null) Language(languageFromCode) else Language.UNKNOWN_LANGUAGE
			val languageTo =
				if (languageToCode != null) Language(languageToCode) else Language.UNKNOWN_LANGUAGE
			Text(content = textContent, language = languageFrom) to languageTo
		} else {
			null
		}
	}

	fun openAbout() {
		navController.navigate(R.id.actionToAbout)
	}

	fun onBackPressed() {
		if (navController.currentDestination?.id in rootDestinationIds) {
			activity.finish()
		} else {
			navController.popBackStack()
		}
	}
}