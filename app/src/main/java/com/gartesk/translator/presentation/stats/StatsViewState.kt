package com.gartesk.translator.presentation.stats

import com.gartesk.translator.domain.entity.Translation

sealed class StatsViewState

data class IdleStatsViewState(val translations: List<Translation>) : StatsViewState()

object EmptyStatsViewState : StatsViewState()

object ErrorStatsViewState : StatsViewState()

object LoadingStatsViewState : StatsViewState()