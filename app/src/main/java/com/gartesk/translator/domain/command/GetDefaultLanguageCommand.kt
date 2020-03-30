package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Stat
import com.gartesk.translator.domain.repository.DeviceRepository
import com.gartesk.translator.domain.repository.StatsRepository
import io.reactivex.Maybe
import io.reactivex.Single

class GetDefaultLanguageCommand(
	private val statsRepository: StatsRepository,
	private val deviceRepository: DeviceRepository
) {

	fun execute(): Single<Language> =
		statsRepository.observeAll()
			.firstElement()
			.flatMap { stats ->
				val popularLanguage = stats.getPopularLanguage()
				if (popularLanguage != null) {
					Maybe.just(popularLanguage)
				} else {
					Maybe.empty()
				}
			}
			.switchIfEmpty(deviceRepository.getLanguage())

	private fun List<Stat>.getPopularLanguage(): Language? =
		flatMap { it.counters }
			.groupBy { counter -> counter.language }
			.maxBy { it.value.size }
			?.key
}