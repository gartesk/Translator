package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.StatsRepository
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class GetTranslationCommand(
	private val translationRepository: TranslationRepository,
	private val statsRepository: StatsRepository
) {

	fun execute(textFrom: Text, languageTo: Language): Single<Translation> =
		translationRepository.translate(textFrom, languageTo)
			.flatMap { textTo ->
				statsRepository.increment(textFrom, textTo)
					.andThen(statsRepository.get(textFrom, textTo))
			}
			.subscribeOn(Schedulers.io())
}