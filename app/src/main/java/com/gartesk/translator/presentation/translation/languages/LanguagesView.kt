package com.gartesk.translator.presentation.translation.languages

import com.gartesk.mosbyx.mvi.MviView

interface LanguagesView : MviView {
	fun render(viewState: LanguagesViewState)
}