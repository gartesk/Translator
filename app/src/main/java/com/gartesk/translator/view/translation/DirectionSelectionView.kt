package com.gartesk.translator.view.translation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Direction
import com.gartesk.translator.domain.entity.Language
import kotlinx.android.synthetic.main.item_language.view.*
import kotlinx.android.synthetic.main.view_direction_selection.view.*

class DirectionSelectionView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
	defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

	private val fromAdapter: LanguagesAdapter
	private val toAdapter: LanguagesAdapter

	private var directionsRaw: List<Direction> = emptyList()
	private var directions: List<Direction> = emptyList()

	private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onNothingSelected(parent: AdapterView<*>) {
			val selectedDirection = getSelectedDirection()
			setDirectionsActual(directionsRaw, selectedDirection)
			selectDirectionActual(selectedDirection.from, selectedDirection.to)
		}

		override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
			val selectedDirection = getSelectedDirection()
			setDirectionsActual(directionsRaw, selectedDirection)
			selectDirectionActual(selectedDirection.from, selectedDirection.to)
		}
	}

	init {
		inflate(context, R.layout.view_direction_selection, this)
		fromAdapter = LanguagesAdapter(context)
		languageFromSpinner.adapter = fromAdapter
		languageFromSpinner.onItemSelectedListener = onItemSelectedListener
		toAdapter = LanguagesAdapter(context)
		languageToSpinner.adapter = toAdapter
		languageToSpinner.onItemSelectedListener = onItemSelectedListener
		swapLanguageButton.setOnClickListener {
			val selectedDirection = getSelectedDirection()
			selectDirection(selectedDirection.to, Language.UNKNOWN_LANGUAGE)
			selectDirection(selectedDirection.to, selectedDirection.from)
		}
	}

	fun setDirections(directions: List<Direction>) {
		val selectedDirection = getSelectedDirection()
		setDirectionsActual(directions, selectedDirection)
		selectDirectionActual(selectedDirection.from, selectedDirection.to)
	}

	private fun setDirectionsActual(directions: List<Direction>, selectedDirection: Direction) {
		directionsRaw = directions
		val languagesFrom = directions
			.map { it.from }
			.distinct()
			.filter { it != Language.UNKNOWN_LANGUAGE }
			.sortedBy { it.code }
		val partialDirectionsFrom = languagesFrom.map { Direction(it, Language.UNKNOWN_LANGUAGE) }
		val languagesTo = directions
			.map { it.to }
			.distinct()
			.filter { it != Language.UNKNOWN_LANGUAGE }
			.sortedBy { it.code }
		val partialDirectionsTo = languagesTo.map { Direction(Language.UNKNOWN_LANGUAGE, it) }
		this.directions = listOf(Direction.UNKNOWN_DIRECTION) +
				directionsRaw + partialDirectionsFrom + partialDirectionsTo
		fromAdapter.objects = (listOf(Language.UNKNOWN_LANGUAGE) + languagesFrom)
			.map {
				val selected = it == selectedDirection.from
				LanguageHolder(it, selected)
			}
			.toTypedArray()
		toAdapter.objects = (listOf(Language.UNKNOWN_LANGUAGE) + languagesTo)
			.map {
				val enabled = this.directions.contains(Direction(selectedDirection.from, it))
				val selected = it == selectedDirection.to
				LanguageHolder(it, selected) to enabled
			}
			.filter { it.second }
			.map { it.first }
			.toTypedArray()
	}

	fun selectDirection(languageFrom: Language, languageTo: Language) {
		selectDirectionActual(languageFrom, languageTo)
		setDirectionsActual(this.directions, Direction(languageFrom, languageTo))
	}

	private fun selectDirectionActual(languageFrom: Language, languageTo: Language) {
		val fromIndex = fromAdapter.objects.indexOfFirst { it.language == languageFrom }
		val toIndex = toAdapter.objects.indexOfFirst { it.language == languageTo }
		var fromIndexToSelect = fromIndex
		var toIndexToSelect = toIndex
		if (fromIndex != AdapterView.INVALID_POSITION) {
			if (!directions.contains(Direction(languageFrom, languageTo))) {
				toIndexToSelect = 0
			}
		} else {
			fromIndexToSelect = 0
		}
		if (toIndex == AdapterView.INVALID_POSITION) {
			toIndexToSelect = 0
		}
		languageFromSpinner.setSelection(fromIndexToSelect)
		languageToSpinner.setSelection(toIndexToSelect)
	}

	fun getSelectedDirection(): Direction {
		val selectedLanguageFrom = (languageFromSpinner.selectedItem as? LanguageHolder)?.language
			?: Language.UNKNOWN_LANGUAGE
		val selectedLanguageTo = (languageToSpinner.selectedItem as? LanguageHolder)?.language
			?: Language.UNKNOWN_LANGUAGE
		return Direction(selectedLanguageFrom, selectedLanguageTo)
	}

	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		languageFromSpinner.isEnabled = enabled
		languageToSpinner.isEnabled = enabled
	}
}

private class LanguagesAdapter(
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
		val view = convertView ?: createView(parent)
		view.languageName.text = objects[position].language.code
			?: context.getString(R.string.language_default)
		view.languageSelectedIcon.visibility = if (objects[position].selected) VISIBLE else GONE
		view.languageSeparator.visibility = VISIBLE
		view.languageSeparator.visibility = if (position == objects.size - 1) GONE else VISIBLE
		return view
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val view = convertView ?: createView(parent)
		view.languageName.text = objects[position].language.code
			?: context.getString(R.string.language_default)
		view.languageSelectedIcon.visibility = GONE
		view.languageSeparator.visibility = GONE
		return view
	}

	private fun createView(parent: ViewGroup?): View =
		LayoutInflater.from(context).inflate(R.layout.item_language, parent, false)

	override fun getItem(position: Int): LanguageHolder =
		objects[position]

	override fun getItemId(position: Int): Long =
		objects[position].hashCode().toLong()

	override fun getCount(): Int =
		objects.size
}

private data class LanguageHolder(
	val language: Language,
	val selected: Boolean
)