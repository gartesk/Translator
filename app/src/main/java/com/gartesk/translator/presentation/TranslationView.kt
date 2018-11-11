package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface TranslationView : MvpView {
    fun translationIntent(): Observable<Unit>
    fun cancellationIntent(): Observable<Unit>
    fun textIntent(): Observable<String>
    fun languageFromIntent(): Observable<Language>
    fun languageToIntent(): Observable<Language>
    fun render(viewState: FullTranslationViewState)
}