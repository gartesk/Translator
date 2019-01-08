package com.gartesk.translator.view

import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.presentation.LanguagesPresenter
import com.gartesk.translator.presentation.LanguagesView
import com.gartesk.translator.presentation.LanguagesViewState
import com.gartesk.translator.view.core.DelegatedMviFragment
import com.gartesk.translator.view.core.DelegatingMviView

class DelegatingLanguagesView(
    private val translationFragment: TranslationFragment
) : DelegatingMviView<LanguagesView, LanguagesPresenter>(translationFragment), LanguagesView {

    override fun createPresenter(): LanguagesPresenter {
        val commandFactory =
            (delegatedFragment.requireActivity().application as TranslatorApplication).commandFactory
        return LanguagesPresenter(
            commandFactory.createListLanguagesCommand()
        )
    }

    override fun render(viewState: LanguagesViewState) {
        translationFragment.languageFromAdapter.objects = viewState.languages.toTypedArray()
        translationFragment.languageToAdapter.objects = viewState.languages.toTypedArray()
    }
}