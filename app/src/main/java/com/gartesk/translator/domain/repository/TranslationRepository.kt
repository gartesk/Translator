package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Translation
import io.reactivex.Single

interface TranslationRepository {
	fun translate(textFrom: String, languageTo: Language): Single<Translation>

	fun getLanguages(): Single<List<Language>>
}