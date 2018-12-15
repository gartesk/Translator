package com.gartesk.translator.view

import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.presentation.LanguagesPresenter
import com.gartesk.translator.presentation.LanguagesView
import com.gartesk.translator.presentation.LanguagesViewState
import com.gartesk.translator.view.core.DelegatedMviFragment
import com.gartesk.translator.view.core.DelegatingMviView

class DelegatingLanguagesView(
    delegatedMviFragment: DelegatedMviFragment<*, *>
) : DelegatingMviView<LanguagesView, LanguagesPresenter>(delegatedMviFragment), LanguagesView {

    override fun createPresenter(): LanguagesPresenter {
        val commandFactory =
            (delegatedFragment.requireActivity().application as TranslatorApplication).commandFactory
        return LanguagesPresenter(
            commandFactory.createListLanguagesCommand()
        )
    }

    override fun render(viewState: LanguagesViewState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}