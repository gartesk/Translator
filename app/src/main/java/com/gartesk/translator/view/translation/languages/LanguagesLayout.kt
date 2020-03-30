package com.gartesk.translator.view.translation.languages

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import com.gartesk.mosbyx.mvi.MviLinearLayout
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.*
import com.gartesk.translator.presentation.translation.languages.LanguagesPresenter
import com.gartesk.translator.presentation.translation.languages.LanguagesView
import com.gartesk.translator.presentation.translation.languages.LanguagesViewState
import com.gartesk.translator.view.commandFactory
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.item_language.view.*
import kotlinx.android.synthetic.main.view_languages.view.*

class LanguagesLayout @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
	defStyleRes: Int = 0
) : MviLinearLayout<LanguagesView, LanguagesPresenter>(context, attrs, defStyleAttr, defStyleRes),
	LanguagesView {

	private val directionSelectionSubject = BehaviorSubject.create<Direction>()

	private val fromAdapter: LanguagesAdapter
	private val toAdapter: LanguagesAdapter

	private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onNothingSelected(parent: AdapterView<*>) {
			directionSelectionSubject.onNext(getSelectedDirection())
		}

		override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
			directionSelectionSubject.onNext(getSelectedDirection())
		}
	}

	init {
		inflate(context, R.layout.view_languages, this)

		fromAdapter = LanguagesAdapter(context)
		languageFromSpinner.adapter = fromAdapter
		languageFromSpinner.onItemSelectedListener = onItemSelectedListener

		toAdapter = LanguagesAdapter(context)
		languageToSpinner.adapter = toAdapter
		languageToSpinner.onItemSelectedListener = onItemSelectedListener

		swapLanguageButton.setOnClickListener {
			val selectedDirection = getSelectedDirection()
			directionSelectionSubject.onNext(selectedDirection.reverted)
		}
	}

	fun getSelectedDirection(): Direction {
		val selectedLanguageFrom = (languageFromSpinner.selectedItem as? LanguageHolder)?.language
			?: Language.UNKNOWN_LANGUAGE
		val selectedLanguageTo = (languageToSpinner.selectedItem as? LanguageHolder)?.language
			?: Language.UNKNOWN_LANGUAGE
		return Direction(selectedLanguageFrom, selectedLanguageTo)
	}

	fun setSelectedDirection(direction: Direction) {
		directionSelectionSubject.onNext(direction)
	}

	override fun createPresenter(): LanguagesPresenter =
		LanguagesPresenter(commandFactory.createGetDirectionsCommand())

	override fun directionSelectionIntent(): Observable<Direction> =
		directionSelectionSubject.distinctUntilChanged()

	override fun render(viewState: LanguagesViewState) {
		setDirections(viewState.directions, viewState.selectedDirection)
	}

	private fun setDirections(directions: List<Direction>, selectedDirection: Direction) {
		fromAdapter.objects =
			(listOf(Language.UNKNOWN_LANGUAGE) + directions.languagesFrom)
				.map {
					val selected = it == selectedDirection.from
					LanguageHolder(it, selected)
				}
				.toTypedArray()

		toAdapter.objects = (listOf(Language.UNKNOWN_LANGUAGE) + directions.languagesTo)
			.map {
				val enabled = directions.contains(Direction(selectedDirection.from, it))
				val selected = it == selectedDirection.to
				LanguageHolder(it, selected) to enabled
			}
			.filter { it.second }
			.map { it.first }
			.toTypedArray()

		setSelection(directions, selectedDirection)
	}

	private fun setSelection(directions: List<Direction>, selectedDirection: Direction) {
		val fromIndex = fromAdapter.objects.indexOfFirst { it.language == selectedDirection.from }
		val toIndex = toAdapter.objects.indexOfFirst { it.language == selectedDirection.to }
		var fromIndexToSelect = fromIndex
		var toIndexToSelect = toIndex
		if (fromIndex != AdapterView.INVALID_POSITION) {
			if (!directions.contains(selectedDirection)) {
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
		view.languageName.text = if (objects[position].language == Language.UNKNOWN_LANGUAGE) {
			context.getString(R.string.language_default)
		} else {
			objects[position].language.code
		}
		view.languageSelectedIcon.visibility = if (objects[position].selected) VISIBLE else GONE
		view.languageSeparator.visibility = VISIBLE
		view.languageSeparator.visibility = if (position == objects.size - 1) GONE else VISIBLE
		return view
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val view = convertView ?: createView(parent)
		view.languageName.text = if (objects[position].language == Language.UNKNOWN_LANGUAGE) {
			context.getString(R.string.language_default)
		} else {
			objects[position].language.code
		}
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