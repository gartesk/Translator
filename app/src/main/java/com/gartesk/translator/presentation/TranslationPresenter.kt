package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.MaybeCommand
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class TranslationPresenter(
    private val translateStringCommand: MaybeCommand<String, String>
) : MviBasePresenter<TranslationView, TranslationViewState>() {

    override fun bindIntents() {
        val translation = intent(TranslationView::translationIntent)
            .switchMap(::translate)
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(translation, TranslationView::render)
    }

    private fun translate(query: String): Observable<TranslationViewState> {
        if (query.isEmpty()) {
            return Observable.just(EmptyTranslationViewState(query))
        }
        return translateStringCommand.execute(query)
            .map<TranslationViewState> { ResultTranslationViewState(query, it) }
            .defaultIfEmpty(EmptyTranslationViewState(query))
            .toObservable()
            .startWith(LoadingTranslationViewState(query))
            .onErrorReturn { EmptyTranslationViewState(query) }
    }
}