package com.gartesk.translator.data.stats

import android.content.Context
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Stat
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.repository.StatsRepository
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ObjectBoxStatsRepository(context: Context) : StatsRepository {

	private val statsBox = MyObjectBox.builder()
		.androidContext(context)
		.build()
		.boxFor<StatModel>()

	override fun increment(textFrom: Text, languageTo: Language): Completable =
		Completable.fromAction {
			val existingStat = findStatModel(textFrom, languageTo)

			val updatedStat =
				existingStat?.copy(
					counter = existingStat.counter + 1
				) ?: StatModel(
					textFrom = textFrom.content,
					languageFrom = textFrom.language.code,
					languageTo = languageTo.code,
					counter = 1
				)

			statsBox.put(updatedStat)
		}
			.subscribeOn(Schedulers.io())

	private fun findStatModel(textFrom: Text, languageTo: Language): StatModel? =
		statsBox.query()
			.equal(StatModel_.textFrom, textFrom.content)
			.equal(StatModel_.languageFrom, textFrom.language.code)
			.equal(StatModel_.languageTo, languageTo.code)
			.build()
			.findFirst()

	override fun get(textFrom: Text): Single<Stat> =
		Single.fromCallable { findStatModels(textFrom) }
			.map { statModels -> Stat(textFrom, statModels.getCounters()) }
			.subscribeOn(Schedulers.io())

	private fun findStatModels(textFrom: Text): List<StatModel> =
		statsBox.query()
			.equal(StatModel_.textFrom, textFrom.content)
			.equal(StatModel_.languageFrom, textFrom.language.code)
			.orderDesc(StatModel_.counter)
			.build()
			.find()

	override fun observeAll(): Observable<List<Stat>> =
		RxQuery.observable(statsBox.query().orderDesc(StatModel_.counter).build())
			.map { statModels ->
				statModels.groupBy { statModel ->
					Text(statModel.textFrom, Language(statModel.languageFrom))
				}
					.map { (from, statModels) ->
						Stat(from, statModels.getCounters())
					}
			}
			.subscribeOn(Schedulers.io())

	private fun List<StatModel>.getCounters(): List<Stat.Counter> =
		map { statModel ->
			Stat.Counter(
				language = Language(statModel.languageTo),
				value = statModel.counter
			)
		}
}