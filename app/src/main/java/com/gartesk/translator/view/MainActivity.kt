package com.gartesk.translator.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavArgument
import androidx.navigation.fragment.findNavController
import com.gartesk.translator.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	val navigator = Navigator(this)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initNavGraph()

		navigationView.setOnNavigationItemSelectedListener {
			val currentId = navHostFragment.findNavController().currentDestination?.id
			when {
				it.itemId == R.id.translation && currentId != R.id.translationFragment ->
					openTranslation(null)

				it.itemId == R.id.stats && currentId != R.id.statsFragment ->
					navHostFragment.findNavController().navigate(R.id.actionToStatsFragment)
			}
			true
		}
	}

	private fun initNavGraph() {
		val navController = navHostFragment.findNavController()
		val navInflater = navController.navInflater
		val graph = navInflater.inflate(R.navigation.nav_graph)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val textToProcess = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
			val textToProcessArgument = NavArgument.Builder()
				.setDefaultValue(textToProcess)
				.setIsNullable(true)
				.build()
			graph.addArgument(Intent.EXTRA_PROCESS_TEXT, textToProcessArgument)
		}
		navController.graph = graph
	}

	override fun onBackPressed() {
		finish()
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		openTranslation(intent.extras)
		navigationView.selectedItemId = R.id.translation
	}

	private fun openTranslation(bundle: Bundle?) {
		navHostFragment.findNavController().navigate(R.id.actionToTranslationFragment, bundle)
	}
}