package com.gartesk.translator.presentation

import com.gartesk.mosbyx.mvi.MviView

interface LanguagesView : MviView {
    fun render(viewState: LanguagesViewState)
}