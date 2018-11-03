package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface TranslationView : MvpView {
    fun translationIntent(): Observable<Pair<Text, Language>>
    fun cancellationIntent(): Observable<Unit>
    fun render(viewState: TranslationViewState)
}