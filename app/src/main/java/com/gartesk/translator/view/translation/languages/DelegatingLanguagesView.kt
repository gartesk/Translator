package com.gartesk.translator.view.translation.languages

import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.presentation.translation.languages.LanguagesPresenter
import com.gartesk.translator.presentation.translation.languages.LanguagesView
import com.gartesk.translator.presentation.translation.languages.LanguagesViewState
import com.gartesk.translator.view.core.DelegatingMviView
import com.gartesk.translator.view.translation.TranslationFragment
import kotlinx.android.synthetic.main.fragment_translation.*

class DelegatingLanguagesView(
	private val translationFragment: TranslationFragment
) : DelegatingMviView<LanguagesView, LanguagesPresenter>(translationFragment),
	LanguagesView {

	override fun createPresenter(): LanguagesPresenter {
		val commandFactory =
			(delegatedFragment.requireActivity().application as TranslatorApplication).commandFactory
		return LanguagesPresenter(
			commandFactory.createGetDirectionsCommand()
		)
	}

	override fun render(viewState: LanguagesViewState) {
		translationFragment.directionSelection.setDirections(viewState.directions)
	}
}