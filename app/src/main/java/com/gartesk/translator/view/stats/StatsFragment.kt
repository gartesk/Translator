package com.gartesk.translator.view.stats

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import com.gartesk.mosbyx.mvi.MviFragment
import com.gartesk.translator.R
import com.gartesk.translator.databinding.FragmentStatsBinding
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.stats.*
import com.gartesk.translator.view.commandFactory
import com.gartesk.translator.view.navigator
import com.google.android.material.chip.Chip
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class StatsFragment : MviFragment<StatsView, StatsPresenter>(), StatsView {

	private lateinit var binding: FragmentStatsBinding

	private lateinit var adapter: StatsAdapter
	private val selectedLanguageSubject = BehaviorSubject.create<Language>()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentStatsBinding.inflate(inflater)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		adapter = StatsAdapter(::navigateToTranslation)
		binding.statsList.adapter = adapter
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
		binding.statsList.visibility = VISIBLE
		adapter.items = viewState.stats
		refreshFilter(viewState.filters)
		binding.statsEmptyText.visibility = GONE
		binding.statsProgress.visibility = GONE
	}

	private fun renderEmptyState(viewState: EmptyStatsViewState) {
		binding.statsList.visibility = GONE
		refreshFilter(viewState.filters)
		binding.statsEmptyText.visibility = VISIBLE
		binding.statsProgress.visibility = GONE
	}

	private fun refreshFilter(filters: List<Filter>) {
		binding.filtersScroll.visibility = if (filters.isEmpty()) GONE else VISIBLE
		binding.filtersContainer.removeAllViews()
		filters.forEach { filter ->
			val filterView = LayoutInflater.from(requireContext())
				.inflate(R.layout.item_filter, binding.filtersContainer, false)
			val chip = filterView as Chip
			chip.id = View.generateViewId()
			chip.isChecked = filter.selected
			chip.tag = filter.language
			chip.text = getString(R.string.stats_filter, filter.language.code, filter.counter)
			binding.filtersContainer.addView(filterView)
			chip.setOnCheckedChangeListener { _, checked ->
				if (checked) {
					selectedLanguageSubject.onNext(filter.language)
				}
			}
		}
	}

	private fun renderLoadingState() {
		binding.statsList.visibility = GONE
		binding.filtersScroll.visibility = GONE
		binding.statsEmptyText.visibility = GONE
		binding.statsProgress.visibility = VISIBLE
	}
}