package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface StatsRepository {
	fun increment(textFrom: Text, textTo: Text): Completable

	fun get(textFrom: Text, textTo: Text): Single<Translation>

	fun observeAll(): Observable<List<Translation>>
}