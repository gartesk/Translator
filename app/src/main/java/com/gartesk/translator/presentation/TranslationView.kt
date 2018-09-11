package com.gartesk.translator.presentation

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface TranslationView : MvpView {
    fun translationIntent(): Observable<String>
    fun render(viewState: TranslationViewState)
}