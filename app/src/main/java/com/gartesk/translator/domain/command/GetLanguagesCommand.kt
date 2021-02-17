package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single

class GetLanguagesCommand(
	private val translationRepository: TranslationRepository
) {

	fun execute(): Single<List<Language>> =
		translationRepository.getLanguages()
}