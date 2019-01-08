package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Counter
import com.gartesk.translator.domain.entity.Text
import io.reactivex.Completable
import io.reactivex.Observable

interface CounterRepository {
    fun increment(text: Text): Completable

    fun getUpdates(text: Text): Observable<Counter>
}