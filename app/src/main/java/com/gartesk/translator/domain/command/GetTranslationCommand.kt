package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.entity.toTranslation
import com.gartesk.translator.domain.repository.StatsRepository
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single

class GetTranslationCommand(
	private val translationRepository: TranslationRepository,
	private val statsRepository: StatsRepository
) {

	fun execute(textFrom: Text, languageTo: Language): Single<Translation> =
		translationRepository.translate(textFrom, languageTo)
			.flatMap { (languageFrom, textTo) ->
				val updatedTextFrom = textFrom.copy(language = languageFrom)
				statsRepository.increment(updatedTextFrom, languageTo)
					.andThen(statsRepository.get(updatedTextFrom))
					.map { it.toTranslation(textTo.content, languageTo) }
			}
}