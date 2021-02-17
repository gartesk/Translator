package com.gartesk.translator.presentation.translation

import com.gartesk.mosbyx.mvi.MviView
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import io.reactivex.Observable

interface TranslationView : MviView {
	fun translationIntent(): Observable<String>
	fun cancellationIntent(): Observable<Unit>
	fun languageSelectionIntent(): Observable<Language>
	fun render(viewState: TranslationViewState)
}