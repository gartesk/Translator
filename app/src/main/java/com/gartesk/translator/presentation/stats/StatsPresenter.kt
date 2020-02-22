package com.gartesk.translator.presentation.stats

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.ObserveAllStatsCommand
import io.reactivex.android.schedulers.AndroidSchedulers

class StatsPresenter(private val observeAllStatsCommand: ObserveAllStatsCommand) :
	MviBasePresenter<StatsView, StatsViewState>() {

	override fun bindIntents() {
		val viewStateEmitter = observeAllStatsCommand.execute()
			.map { translations ->
				if (translations.isEmpty()) {
					EmptyStatsViewState
				} else {
					IdleStatsViewState(translations)
				}
			}
			.startWith(LoadingStatsViewState)
			.onErrorReturn { ErrorStatsViewState }
			.observeOn(AndroidSchedulers.mainThread())

		subscribeViewState(viewStateEmitter, StatsView::render)
	}
}