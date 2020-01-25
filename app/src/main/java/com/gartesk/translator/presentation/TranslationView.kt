package com.gartesk.translator.presentation

import com.gartesk.mosbyx.mvi.MviView
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import io.reactivex.Observable

interface TranslationView : MviView {
    fun translationIntent(): Observable<Pair<Text, Language>>
    fun cancellationIntent(): Observable<Unit>
    fun render(viewState: TranslationViewState)
}