package com.gartesk.translator.view.translation.languages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.gartesk.translator.R
import com.gartesk.translator.databinding.ItemLanguageBinding
import com.gartesk.translator.domain.entity.Language

class LanguagesAdapter(
	private val context: Context
) : BaseAdapter() {

	var objects: Array<LanguageHolder> = emptyArray()
		set(value) {
			if (value.contentEquals(field)) {
				return
			}
			field = value
			notifyDataSetChanged()
		}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
		val binding = convertView?.let { ItemLanguageBinding.bind(it) } ?: createItemViewBinding()
		binding.languageName.text = if (objects[position].language == Language.UNKNOWN_LANGUAGE) {
			context.getString(R.string.language_default)
		} else {
			objects[position].language.code
		}
		binding.languageSelectedIcon.visibility = if (objects[position].selected) View.VISIBLE else View.GONE
		binding.languageSeparator.visibility = View.VISIBLE
		binding.languageSeparator.visibility = if (position == objects.size - 1) View.GONE else View.VISIBLE
		return binding.root
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val binding = convertView?.let { ItemLanguageBinding.bind(it) } ?: createItemViewBinding()
		binding.languageName.text = if (objects[position].language == Language.UNKNOWN_LANGUAGE) {
			context.getString(R.string.language_default)
		} else {
			objects[position].language.code
		}
		binding.languageSelectedIcon.visibility = View.GONE
		binding.languageSeparator.visibility = View.GONE
		return binding.root
	}

	private fun createItemViewBinding(): ItemLanguageBinding =
		ItemLanguageBinding.inflate(LayoutInflater.from(context))

	override fun getItem(position: Int): LanguageHolder =
		objects[position]

	override fun getItemId(position: Int): Long =
		objects[position].hashCode().toLong()

	override fun getCount(): Int =
		objects.size
}

data class LanguageHolder(
	val language: Language,
	val selected: Boolean
)