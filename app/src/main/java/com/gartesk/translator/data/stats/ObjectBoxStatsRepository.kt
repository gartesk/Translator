package com.gartesk.translator.data.stats

import android.content.Context
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.StatsRepository
import io.objectbox.kotlin.boxFor
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ObjectBoxStatsRepository(context: Context) : StatsRepository {

	private val translationBox = MyObjectBox.builder()
		.androidContext(context)
		.build()
		.boxFor<TranslationModel>()

	override fun increment(textFrom: Text, textTo: Text): Completable =
		Completable.fromAction {
			val existingTranslation = findTranslationModel(textFrom, textTo)

			val updatedTranslation =
				existingTranslation?.copy(
					textTo = textTo.content,
					counter = existingTranslation.counter + 1
				) ?: TranslationModel(
					textFrom = textFrom.content,
					languageFrom = textFrom.language.code.orEmpty(),
					textTo = textTo.content,
					languageTo = textTo.language.code.orEmpty(),
					counter = 1
				)

			translationBox.put(updatedTranslation)
		}
			.subscribeOn(Schedulers.io())

	override fun get(textFrom: Text, textTo: Text): Single<Translation> =
		Single.fromCallable {
			findTranslationModel(textFrom, textTo)
				?: throw NoSuchElementException("No translation found for args " +
							"textFrom=$textFrom, textTo=$textTo")
		}
			.map(::mapTranslation)
			.subscribeOn(Schedulers.io())

	private fun findTranslationModel(textFrom: Text, textTo: Text): TranslationModel? =
		translationBox.query()
			.equal(TranslationModel_.textFrom, textFrom.content)
			.equal(TranslationModel_.languageFrom, textFrom.language.code.orEmpty())
			.equal(TranslationModel_.languageTo, textTo.language.code.orEmpty())
			.build()
			.findFirst()

	override fun getAll(): Single<List<Translation>> =
		Single.fromCallable { translationBox.all.map(::mapTranslation) }
			.subscribeOn(Schedulers.io())

	private fun mapTranslation(translationModel: TranslationModel): Translation =
		Translation(
			from = Text(
				content = translationModel.textFrom,
				language = Language(translationModel.languageFrom)
			),
			to = Text(
				content = translationModel.textTo,
				language = Language(translationModel.languageTo)
			),
			counter = translationModel.counter
		)
}