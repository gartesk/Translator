package com.gartesk.translator.presentation.stats

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.ObserveAllStatsCommand
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Stat
import com.gartesk.translator.domain.entity.languages
import com.gartesk.translator.domain.entity.totalCounter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction

class StatsPresenter(private val observeAllStatsCommand: ObserveAllStatsCommand) :
	MviBasePresenter<StatsView, StatsViewState>() {

	override fun bindIntents() {
		val statsEmitter = observeAllStatsCommand.execute().share()

		val defaultSelectedLanguageEmitter = statsEmitter.firstOrError()
			.map { stats ->
				stats.languages.maxBy { language ->
					stats.countFor(language)
				}
			}

		val selectedLanguageEmitter = intent(StatsView::selectedLanguageIntent)
			.startWith(defaultSelectedLanguageEmitter.toObservable())

		val viewStateEmitter = Observable.combineLatest(
			statsEmitter,
			selectedLanguageEmitter,
			BiFunction<List<Stat>, Language, StatsViewState> { stats, selectedLanguage ->
				createViewState(stats, selectedLanguage)
			}
		)
			.startWith(LoadingStatsViewState)
			.observeOn(AndroidSchedulers.mainThread())

		subscribeViewState(viewStateEmitter, StatsView::render)
	}

	private fun createViewState(stats: List<Stat>, selectedLanguage: Language): StatsViewState {
		val filters = stats.languages
			.map { language ->
				val selected = language == selectedLanguage
				val counter = stats.countFor(language)
				Filter(language, counter, selected)
			}

		val selectedStats = stats.filter { it.from.language == selectedLanguage }

		return if (selectedStats.isEmpty()) {
			EmptyStatsViewState(filters)
		} else {
			IdleStatsViewState(selectedStats, filters)
		}
	}

	private fun List<Stat>.countFor(language: Language): Int =
		fold(0) { acc, stat ->
			if (stat.from.language == language) {
				acc + stat.totalCounter
			} else {
				acc
			}
		}
}