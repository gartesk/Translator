package com.gartesk.translator.presentation.stats

import com.gartesk.mosbyx.mvi.MviView

interface StatsView : MviView {
	fun render(viewState: StatsViewState)
}