package com.gartesk.translator.presentation.stats

import com.gartesk.translator.domain.entity.Stat

sealed class StatsViewState

data class IdleStatsViewState(val stats: List<Stat>) : StatsViewState()

object EmptyStatsViewState : StatsViewState()

object LoadingStatsViewState : StatsViewState()