package com.gartesk.translator.data.translation

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class MockTranslationRepository : TranslationRepository {

	override fun translate(textFrom: String, languageTo: Language): Single<Translation> =
		Single.just(
			Translation(
				from = Text(textFrom, Language("kek")),
				to = Text("$textFrom to ${languageTo.code}", languageTo)
			)
		)
			.subscribeOn(Schedulers.io())

	override fun getLanguages(): Single<List<Language>> =
		Single.just(
			listOf(
				Language("ru"),
				Language("fr"),
				Language("en")
			)
		)
			.subscribeOn(Schedulers.io())
}