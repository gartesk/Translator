package com.gartesk.translator.presentation.stats

import com.gartesk.mosbyx.mvi.MviView
import com.gartesk.translator.domain.entity.Language
import io.reactivex.Observable

interface StatsView : MviView {
	fun selectedLanguageIntent(): Observable<Language>
	fun render(viewState: StatsViewState)
}