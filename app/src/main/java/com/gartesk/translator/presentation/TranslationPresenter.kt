package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.ObservableCommand
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
    private val listLanguagesCommand: ObservableCommand<Unit, List<Language>>
) : MviBasePresenter<TranslationView, FullTranslationViewState>() {

    override fun bindIntents() {
        val cancellation = intent(TranslationView::cancellationIntent)
        val translation = intent(TranslationView::translationIntent)

        val textEmitter = intent(TranslationView::textIntent)
            .map { ChangedTextTranslationViewState(it) }
        val languageFromEmitter = intent(TranslationView::languageFromIntent)
            .map { SelectedLanguageFromTranslationViewState(it) }
        val languageToEmitter = intent(TranslationView::languageToIntent)
            .map { SelectedLanguageToTranslationViewState(it) }
        val languagesEmitter = listLanguagesCommand.execute(Unit)
            .map { LoadedLanguagesTranslationViewState(it) }

        val translationEmitter = translation
            .switchMap { (textFrom, languageTo) -> translate(textFrom, languageTo, cancellation) }

        val viewStateEmitter =
            Observable.merge<TranslationViewState>(translationEmitter, languagesEmitter)
                .scan { oldViewState, newViewState ->
                    return@scan if (newViewState is PartialTranslationViewState) {
                        oldViewState.copyWithLanguages(newViewState.languages)
                    } else {
                        newViewState.copyWithLanguages(oldViewState.languages)
                    }
                }
                .cast(FullTranslationViewState::class.java)
                .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(viewStateEmitter, TranslationView::render)
    }

    private fun translate(
        textFrom: Text,
        languageTo: Language,
        cancellation: Observable<Unit>
    ): Observable<FullTranslationViewState> {
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
        val argument = textFrom to languageTo
        return translateTextToLanguageCommand.execute(argument)
            .toObservable()
            .takeUntil(cancellation)
            .map<FullTranslationViewState> {
                IdleTranslationViewState(textFrom, it.to)
            }
            .defaultIfEmpty(IdleTranslationViewState(textFrom, Text(language = languageTo)))
            .startWith(LoadingTranslationViewState(textFrom, Text(language = languageTo)))
            .onErrorReturn {
                ErrorTranslationViewState(
                    textFrom,
                    Text(language = languageTo),
                    ErrorType.CONNECTION
                )
            }
    }
}