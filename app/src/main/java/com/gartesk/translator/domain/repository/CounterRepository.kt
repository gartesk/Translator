package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import io.reactivex.Completable
import io.reactivex.Single

interface CounterRepository {
	fun increment(textFrom: Text, languageTo: Language): Completable

	fun get(textFrom: Text, languageTo: Language): Single<Int>
}