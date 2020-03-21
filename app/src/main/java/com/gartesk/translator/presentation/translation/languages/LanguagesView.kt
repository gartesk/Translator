package com.gartesk.translator.presentation.translation.languages

import com.gartesk.mosbyx.mvi.MviView
import com.gartesk.translator.domain.entity.Direction
import io.reactivex.Observable

interface LanguagesView : MviView {
	fun directionSelectionIntent(): Observable<Direction>

	fun render(viewState: LanguagesViewState)
}