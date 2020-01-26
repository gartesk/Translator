package com.gartesk.translator.presentation

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.GetTranslationCommand
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.ErrorTranslationViewState.ErrorType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class TranslationPresenter(
    private val getTranslationCommand: GetTranslationCommand
) : MviBasePresenter<TranslationView, TranslationViewState>() {

    override fun bindIntents() {
        val cancellation = intent(TranslationView::cancellationIntent)
        val translation = intent(TranslationView::translationIntent)

        val viewStateEmitter = translation
            .concatMap { (textFrom, languageTo) -> translate(textFrom, languageTo, cancellation) }
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(viewStateEmitter, TranslationView::render)
    }

    private fun translate(
        textFrom: Text,
        languageTo: Language,
        cancellation: Observable<Unit>
    ): Observable<TranslationViewState> {
        if (textFrom.content.isEmpty()) {
            return Observable.just(
                ErrorTranslationViewState(
                    textFrom,
                    Text(language = languageTo),
                    ErrorType.EMPTY_TEXT
                )
            )
        } else if (languageTo == Language.UNKNOWN_LANGUAGE) {
            return Observable.just(
                ErrorTranslationViewState(
                    textFrom,
                    Text(language = languageTo),
                    ErrorType.TARGET_LANGUAGE
                )
            )
        }

        return getTranslationCommand.execute(textFrom, languageTo)
            .toObservable()
            .takeUntil(cancellation)
            .map<TranslationViewState> {
                IdleTranslationViewState(
                    it.from,
                    it.to,
                    it.counter
                )
            }
            .defaultIfEmpty(
                IdleTranslationViewState(
                    textFrom,
                    Text(language = languageTo)
                )
            )
            .startWith(
                LoadingTranslationViewState(
                    textFrom,
                    Text(language = languageTo)
                )
            )
            .onErrorReturn {
                ErrorTranslationViewState(
                    textFrom,
                    Text(language = languageTo),
                    ErrorType.CONNECTION
                )
            }
    }
}