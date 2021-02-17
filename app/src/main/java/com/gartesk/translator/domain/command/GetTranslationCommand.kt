package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.*
import com.gartesk.translator.domain.repository.StatsRepository
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single

class GetTranslationCommand(
	private val translationRepository: TranslationRepository,
	private val statsRepository: StatsRepository
) {

	fun execute(textFrom: String, languageTo: Language): Single<CountedTranslation> =
		translationRepository.translate(textFrom, languageTo)
			.flatMap { translation ->
				statsRepository.increment(translation.from, translation.to.language)
					.andThen(statsRepository.get(translation.from))
					.map { it.toCountedTranslation(translation.to) }
			}
}

private fun Stat.toCountedTranslation(textTo: Text): CountedTranslation =
	CountedTranslation(
		from = from,
		to = textTo,
		counter = counters
			.firstOrNull { it.language == textTo.language }
			?.value ?: 0
	)