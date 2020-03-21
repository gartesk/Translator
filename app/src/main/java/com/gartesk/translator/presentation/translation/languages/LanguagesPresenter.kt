package com.gartesk.translator.presentation.translation.languages

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.GetDirectionsCommand
import com.gartesk.translator.domain.entity.Direction
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.languagesFrom
import com.gartesk.translator.domain.entity.languagesTo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction

class LanguagesPresenter(
	private val getDirectionsCommand: GetDirectionsCommand
) : MviBasePresenter<LanguagesView, LanguagesViewState>() {

	override fun bindIntents() {
		val directionSelection = intent(LanguagesView::directionSelectionIntent)
			.startWith(Direction.UNKNOWN_DIRECTION)

		val languagesEmitter = getDirectionsCommand.execute()
			.toObservable()
			.onErrorReturn { listOf(Direction.UNKNOWN_DIRECTION) }
			.startWith(listOf(Direction.UNKNOWN_DIRECTION))

		val viewStateEmitter = Observable.combineLatest(
				languagesEmitter,
				directionSelection,
				BiFunction<List<Direction>, Direction, LanguagesViewState> { defaultDirections, selectedDirection ->
					createViewState(defaultDirections, selectedDirection)
				}
			)
			.observeOn(AndroidSchedulers.mainThread())

		subscribeViewState(viewStateEmitter, LanguagesView::render)
	}

	private fun createViewState(
		defaultDirections: List<Direction>,
		selectedDirection: Direction
	): LanguagesViewState {
		val extendedDirections =
			if (defaultDirections.contains(selectedDirection)) {
				defaultDirections
			} else {
				defaultDirections + selectedDirection
			}

		val partialDirectionsFrom = defaultDirections.languagesFrom
			.map { Direction(it, Language.UNKNOWN_LANGUAGE) }

		val partialDirectionsTo = defaultDirections.languagesTo
			.map { Direction(Language.UNKNOWN_LANGUAGE, it) }

		val totalDirections = listOf(Direction.UNKNOWN_DIRECTION) +
				extendedDirections + partialDirectionsFrom + partialDirectionsTo

		return LanguagesViewState(totalDirections, selectedDirection)
	}
}