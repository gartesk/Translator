package com.gartesk.translator.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gartesk.translator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	lateinit var binding: ActivityMainBinding
	lateinit var navigator: Navigator

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

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