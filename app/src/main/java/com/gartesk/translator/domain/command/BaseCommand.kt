package com.gartesk.translator.domain.command

import io.reactivex.*

interface ObservableCommand<T, R> {
    fun execute(argument: T): Observable<R>
}

interface FlowableCommand<T, R> {
    fun execute(argument: T): Flowable<R>
}

interface SingleCommand<T, R> {
    fun execute(argument: T): Single<R>
}

interface MaybeCommand<T, R> {
    fun execute(argument: T): Maybe<R>
}

interface CompletableCommand<T> {
    fun execute(argument: T): Completable
}