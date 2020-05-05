package com.gartesk.translator.view.stats

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import com.gartesk.mosbyx.mvi.MviFragment
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.stats.*
import com.gartesk.translator.view.commandFactory
import com.gartesk.translator.view.navigator
import com.google.android.material.chip.Chip
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : MviFragment<StatsView, StatsPresenter>(), StatsView {

	private lateinit var adapter: StatsAdapter
	private val selectedLanguageSubject = BehaviorSubject.create<Language>()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_stats, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		adapter = StatsAdapter(::navigateToTranslation)
		statsList.adapter = adapter
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.common, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		if (item.itemId == R.id.about) {
			navigator.openAbout()
			true
		} else {
			super.onOptionsItemSelected(item)
		}

	private fun navigateToTranslation(text: Text, languageTo: Language) {
		navigator.openTranslation(text, languageTo)
	}

	override fun createPresenter(): StatsPresenter =
		StatsPresenter(commandFactory.createObserveAllStatsCommand())

	override fun selectedLanguageIntent(): Observable<Language> =
		selectedLanguageSubject.hide().distinctUntilChanged()

	override fun render(viewState: StatsViewState) {
		when (viewState) {
			is IdleStatsViewState -> renderIdleState(viewState)
			is EmptyStatsViewState -> renderEmptyState(viewState)
			LoadingStatsViewState -> renderLoadingState()
		}
	}

	private fun renderIdleState(viewState: IdleStatsViewState) {
		statsList.visibility = VISIBLE
		adapter.items = viewState.stats
		refreshFilter(viewState.filters)
		statsEmptyText.visibility = GONE
		statsProgress.visibility = GONE
	}

	private fun renderEmptyState(viewState: EmptyStatsViewState) {
		statsList.visibility = GONE
		refreshFilter(viewState.filters)
		statsEmptyText.visibility = VISIBLE
		statsProgress.visibility = GONE
	}

	private fun refreshFilter(filters: List<Filter>) {
		filtersScroll.visibility = if (filters.isEmpty()) GONE else VISIBLE
		filtersContainer.removeAllViews()
		filters.forEach { filter ->
			val filterView = LayoutInflater.from(requireContext())
				.inflate(R.layout.item_filter, filtersContainer, false)
			val chip = filterView as Chip
			chip.id = View.generateViewId()
			chip.isChecked = filter.selected
			chip.tag = filter.language
			chip.text = getString(R.string.stats_filter, filter.language.code, filter.counter)
			filtersContainer.addView(filterView)
			chip.setOnCheckedChangeListener { _, checked ->
				if (checked) {
					selectedLanguageSubject.onNext(filter.language)
				}
			}
		}
	}

	private fun renderLoadingState() {
		statsList.visibility = GONE
		filtersScroll.visibility = GONE
		statsEmptyText.visibility = GONE
		statsProgress.visibility = VISIBLE
	}
}