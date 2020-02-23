package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Stat
import com.gartesk.translator.domain.repository.StatsRepository
import io.reactivex.Observable

class ObserveAllStatsCommand(
	private val statsRepository: StatsRepository
) {

	fun execute(): Observable<List<Stat>> =
		statsRepository.observeAll()
}