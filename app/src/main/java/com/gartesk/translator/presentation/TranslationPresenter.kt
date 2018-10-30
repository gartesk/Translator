package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.MaybeCommand
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class TranslationPresenter(
    private val translateStringCommand: MaybeCommand<String, String>
) : MviBasePresenter<TranslationView, TranslationViewState>() {

    override fun bindIntents() {
        val cancellation = intent(TranslationView::cancellationIntent)
        val translation = intent(TranslationView::translationIntent)

        val viewStateEmitter = translation
            .switchMap { translate(it, cancellation) }
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(viewStateEmitter, TranslationView::render)
    }

    private fun translate(query: String, cancellation: Observable<Unit>): Observable<TranslationViewState> {
        if (query.isEmpty()) {
            return Observable.just(EmptyTranslationViewState(query))
        }
        return translateStringCommand.execute(query)
            .toObservable()
            .takeUntil(cancellation)
            .map<TranslationViewState> { ResultTranslationViewState(query, it) }
            .defaultIfEmpty(EmptyTranslationViewState(query))
            .startWith(LoadingTranslationViewState(query))
            .onErrorReturn { EmptyTranslationViewState(query) }
    }
}