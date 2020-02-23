package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Stat
import com.gartesk.translator.domain.entity.Text
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface StatsRepository {
	fun increment(textFrom: Text, languageTo: Language): Completable

	fun get(textFrom: Text): Single<Stat>

	fun observeAll(): Observable<List<Stat>>
}