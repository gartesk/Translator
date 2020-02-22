package com.gartesk.translator.view.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gartesk.mosbyx.mvi.MviFragment
import com.gartesk.translator.R
import com.gartesk.translator.presentation.stats.*
import com.gartesk.translator.view.getCommandFactory

class StatsFragment : MviFragment<StatsView, StatsPresenter>(), StatsView {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_stats, container, false)

	override fun createPresenter(): StatsPresenter =
		StatsPresenter(getCommandFactory().createObserveAllStatsCommand())

	override fun render(viewState: StatsViewState) {
		when (viewState) {
			is IdleStatsViewState -> renderIdleState(viewState)
			EmptyStatsViewState -> renderEmptyState()
			ErrorStatsViewState -> renderErrorState()
			LoadingStatsViewState -> renderLoadingState()
		}
	}

	private fun renderIdleState(viewState: IdleStatsViewState) {

	}

	private fun renderEmptyState() {

	}

	private fun renderErrorState() {

	}

	private fun renderLoadingState() {

	}
}