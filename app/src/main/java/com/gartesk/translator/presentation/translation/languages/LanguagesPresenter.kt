package com.gartesk.translator.presentation.translation.languages

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.GetDirectionsCommand
import com.gartesk.translator.domain.entity.Direction
import io.reactivex.android.schedulers.AndroidSchedulers

class LanguagesPresenter(
	private val getDirectionsCommand: GetDirectionsCommand
) : MviBasePresenter<LanguagesView, LanguagesViewState>() {

	override fun bindIntents() {
		val languagesEmitter = getDirectionsCommand.execute()
			.toObservable()
			.map {
				LanguagesViewState(
					it
				)
			}
			.onErrorReturn {
				LanguagesViewState(
					listOf(Direction.UNKNOWN_DIRECTION)
				)
			}
			.startWith(
				LanguagesViewState(
					listOf(Direction.UNKNOWN_DIRECTION)
				)
			)
			.observeOn(AndroidSchedulers.mainThread())

		subscribeViewState(languagesEmitter, LanguagesView::render)
	}
}