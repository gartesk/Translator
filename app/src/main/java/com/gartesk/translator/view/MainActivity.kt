package com.gartesk.translator.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.gartesk.translator.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		navigationView.setOnNavigationItemSelectedListener {
			when (it.itemId) {
				R.id.translation -> navHostFragment.findNavController().navigate(R.id.actionToTranslationFragment)
				R.id.stats -> navHostFragment.findNavController().navigate(R.id.actionToStatsFragment)
			}
			true
		}
	}

	override fun onSupportNavigateUp(): Boolean =
		navHostFragment.findNavController().navigateUp()
}