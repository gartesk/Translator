package com.gartesk.translator.view.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.gartesk.mosbyx.mvi.MviFragment
import com.gartesk.translator.R
import com.gartesk.translator.presentation.stats.*
import com.gartesk.translator.view.commandFactory
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : MviFragment<StatsView, StatsPresenter>(), StatsView {

	private lateinit var adapter: StatsAdapter

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_stats, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		adapter = StatsAdapter()
		statsList.adapter = adapter
	}

	override fun createPresenter(): StatsPresenter =
		StatsPresenter(commandFactory.createObserveAllStatsCommand())

	override fun render(viewState: StatsViewState) {
		when (viewState) {
			is IdleStatsViewState -> renderIdleState(viewState)
			EmptyStatsViewState -> renderEmptyState()
			LoadingStatsViewState -> renderLoadingState()
		}
	}

	private fun renderIdleState(viewState: IdleStatsViewState) {
		statsList.visibility = VISIBLE
		adapter.items = viewState.stats
		statsEmptyText.visibility = GONE
		statsProgress.visibility = GONE
	}

	private fun renderEmptyState() {
		statsList.visibility = GONE
		statsEmptyText.visibility = VISIBLE
		statsProgress.visibility = GONE
	}

	private fun renderLoadingState() {
		statsList.visibility = GONE
		statsEmptyText.visibility = GONE
		statsProgress.visibility = VISIBLE
	}
}