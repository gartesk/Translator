package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.SingleCommand
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import com.gartesk.translator.presentation.ErrorTranslationViewState.*
import io.reactivex.android.schedulers.AndroidSchedulers

class TranslationPresenter(
    private val translateTextToLanguageCommand: SingleCommand<Pair<Text, Language>, Translation>,
    private val listLanguagesCommand: SingleCommand<Unit, List<Language>>
) : MviBasePresenter<TranslationView, TranslationViewState>() {

    override fun bindIntents() {
        val cancellation = intent(TranslationView::cancellationIntent)
        val translation = intent(TranslationView::translationIntent)

        val languagesEmitter = listLanguagesCommand.execute(Unit)
            .toObservable()
            .map { LanguagesLoadedTranslationViewState(it) }
        val translationEmitter = translation
            .switchMap { (textFrom, languageTo) -> translate(textFrom, languageTo, cancellation) }

        val viewStateEmitter =
            Observable.merge<TranslationViewState>(translationEmitter, languagesEmitter)
                .scan { oldViewState, newViewState ->
                    return@scan when {
                        oldViewState is LanguagesLoadedTranslationViewState ->
                            newViewState.copyWithLanguages(oldViewState.languages)
                        newViewState is LanguagesLoadedTranslationViewState ->
                            oldViewState.copyWithLanguages(newViewState.languages)
                        else -> newViewState
                    }
                }
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