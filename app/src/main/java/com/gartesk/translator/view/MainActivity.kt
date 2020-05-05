package com.gartesk.translator.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gartesk.translator.R

class MainActivity : AppCompatActivity() {

	lateinit var navigator: Navigator

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		navigator = Navigator(this)
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		navigator.onNewIntent(intent)
	}

	override fun onBackPressed() {
		navigator.onBackPressed()
	}
}