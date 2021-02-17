package com.gartesk.translator.view.translation.languages

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.gartesk.mosbyx.mvi.MviLinearLayout
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

	private val languageSelectionSubject = BehaviorSubject.create<Language>()
	private val languagesAdapter = LanguagesAdapter(context)

	private val onLanguageSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onNothingSelected(parent: AdapterView<*>) {
			languageSelectionSubject.onNext(getSelectedLanguage())
		}

		override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
			languageSelectionSubject.onNext(getSelectedLanguage())
		}
	}

	init {
		binding.languagesSpinner.adapter = languagesAdapter
		binding.languagesSpinner.onItemSelectedListener = onLanguageSelectedListener
	}

	fun selectLanguage(language: Language) {
		val selectionIndex = languagesAdapter.objects.indexOfFirst { it.language == language }
		binding.languagesSpinner.setSelection(selectionIndex)
	}

	fun languageSelection(): Observable<Language> = languageSelectionSubject.distinctUntilChanged()

	override fun createPresenter(): LanguagesPresenter =
		LanguagesPresenter(commandFactory.createGetDirectionsCommand())

	override fun render(viewState: LanguagesViewState) {
		val selectedLanguage = getSelectedLanguage()
		languagesAdapter.objects = viewState.languages
			.map {
				val selected = it == selectedLanguage
				LanguageHolder(it, selected)
			}
			.toTypedArray()
	}

	private fun getSelectedLanguage(): Language =
		(binding.languagesSpinner.selectedItem as? LanguageHolder)
			?.language
			?: Language.UNKNOWN_LANGUAGE

	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		binding.languagesSpinner.isEnabled = enabled
	}
}