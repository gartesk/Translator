package com.gartesk.translator.view.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gartesk.translator.R
import com.gartesk.translator.view.navigator

class AboutFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_about, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		if (item.itemId == android.R.id.home) {
			navigator.onBackPressed()
			true
		} else {
			super.onOptionsItemSelected(item)
		}
}