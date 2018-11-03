package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.MaybeCommand
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import com.gartesk.translator.presentation.ErrorTranslationViewState.*

class TranslationPresenter(
    private val translateTextToLanguageCommand: MaybeCommand<Pair<Text, Language>, Translation>
) : MviBasePresenter<TranslationView, TranslationViewState>() {

    override fun bindIntents() {
        val cancellation = intent(TranslationView::cancellationIntent)
        val translation = intent(TranslationView::translationIntent)

        val viewStateEmitter = translation
            .switchMap { (textFrom, languageTo) -> translate(textFrom, languageTo, cancellation) }
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
                ErrorTranslationViewState(textFrom, languageTo, ErrorType.EMPTY_TEXT)
            )
        } else if (languageTo == Language.UNKNOWN_LANGUAGE) {
            return Observable.just(
                ErrorTranslationViewState(textFrom, languageTo, ErrorType.TARGET_LANGUAGE)
            )
        }
        val argument = textFrom to languageTo
        return translateTextToLanguageCommand.execute(argument)
            .toObservable()
            .takeUntil(cancellation)
            .map<TranslationViewState> {
                ResultTranslationViewState(textFrom, it.to.language, it.to.content)
            }
            .defaultIfEmpty(EmptyTranslationViewState(textFrom, languageTo))
            .startWith(LoadingTranslationViewState(textFrom, languageTo))
            .onErrorReturn { ErrorTranslationViewState(textFrom, languageTo, ErrorType.CONNECTION) }
    }
}