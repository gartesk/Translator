package com.gartesk.translator.presentation

import com.hannesdorfmann.mosby3.mvp.MvpView

interface LanguagesView : MvpView {
    fun render(viewState: LanguagesViewState)
}