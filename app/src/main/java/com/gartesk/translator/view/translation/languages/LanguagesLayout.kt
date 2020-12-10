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
import com.gartesk.translator.databinding.ItemLanguageBinding
import com.gartesk.translator.databinding.ViewLanguagesBinding
import com.gartesk.translator.domain.entity.*
import com.gartesk.translator.presentation.translation.languages.LanguagesPresenter
import com.gartesk.translator.presentation.translation.languages.LanguagesView
import com.gartesk.translator.presentation.translation.languages.LanguagesViewState
import com.gartesk.translator.view.commandFactory
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class LanguagesLayout @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
	defStyleRes: Int = 0
) : MviLinearLayout<LanguagesView, LanguagesPresenter>(context, attrs, defStyleAttr, defStyleRes),
	LanguagesView {

	private var binding: ViewLanguagesBinding = ViewLanguagesBinding.inflate(LayoutInflater.from(context))

	private val directionSelectionSubject = BehaviorSubject.create<Direction>()

	private val fromAdapter = LanguagesAdapter(context)
	private val toAdapter = LanguagesAdapter(context)

	private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onNothingSelected(parent: AdapterView<*>) {
			directionSelectionSubject.onNext(getSelectedDirection())
		}

		override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
			directionSelectionSubject.onNext(getSelectedDirection())
		}
	}

	init {
		binding.languageFromSpinner.adapter = fromAdapter
		binding.languageFromSpinner.onItemSelectedListener = onItemSelectedListener

		binding.languageToSpinner.adapter = toAdapter
		binding.languageToSpinner.onItemSelectedListener = onItemSelectedListener

		binding.swapLanguageButton.setOnClickListener {
			val selectedDirection = getSelectedDirection()
			directionSelectionSubject.onNext(selectedDirection.reverted)
		}
	}

	fun getSelectedDirection(): Direction {
		val selectedLanguageFrom = (binding.languageFromSpinner.selectedItem as? LanguageHolder)
			?.language
			?: Language.UNKNOWN_LANGUAGE
		val selectedLanguageTo = (binding.languageToSpinner.selectedItem as? LanguageHolder)
			?.language
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
		binding.languageFromSpinner.setSelection(fromIndexToSelect)
		binding.languageToSpinner.setSelection(toIndexToSelect)
	}

	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		binding.languageFromSpinner.isEnabled = enabled
		binding.languageToSpinner.isEnabled = enabled
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
		val binding = convertView?.let { ItemLanguageBinding.bind(it) } ?: createItemViewBinding()
		binding.languageName.text = if (objects[position].language == Language.UNKNOWN_LANGUAGE) {
			context.getString(R.string.language_default)
		} else {
			objects[position].language.code
		}
		binding.languageSelectedIcon.visibility = if (objects[position].selected) VISIBLE else GONE
		binding.languageSeparator.visibility = VISIBLE
		binding.languageSeparator.visibility = if (position == objects.size - 1) GONE else VISIBLE
		return binding.root
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val binding = convertView?.let { ItemLanguageBinding.bind(it) } ?: createItemViewBinding()
		binding.languageName.text = if (objects[position].language == Language.UNKNOWN_LANGUAGE) {
			context.getString(R.string.language_default)
		} else {
			objects[position].language.code
		}
		binding.languageSelectedIcon.visibility = GONE
		binding.languageSeparator.visibility = GONE
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

private data class LanguageHolder(
	val language: Language,
	val selected: Boolean
)