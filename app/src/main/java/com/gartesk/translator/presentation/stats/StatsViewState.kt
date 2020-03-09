package com.gartesk.translator.presentation.stats

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Stat

sealed class StatsViewState

data class IdleStatsViewState(val stats: List<Stat>, val filters: List<Filter>) : StatsViewState()

data class EmptyStatsViewState(val filters: List<Filter>) : StatsViewState()

object LoadingStatsViewState : StatsViewState()

data class Filter(val language: Language, val counter: Int, val selected: Boolean)