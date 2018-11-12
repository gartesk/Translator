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
) : MviBasePresenter<TranslationView, TranslationViewState>() {

    override fun bindIntents() {
        val cancellation = intent(TranslationView::cancellationIntent)
        val translation = intent(TranslationView::translationIntent)

        val textEmitter = intent(TranslationView::textIntent)
            .map { ChangedTextPartialStateVisitor(it) }
        val languageFromEmitter = intent(TranslationView::languageFromIntent)
            .map { SelectedLanguageFromPartialStateVisitor(it) }
        val languageToEmitter = intent(TranslationView::languageToIntent)
            .map { SelectedLanguageToPartialStateVisitor(it) }
        val languagesEmitter = listLanguagesCommand.execute(Unit)
            .map { LoadedLanguagesPartialStateVisitor(it) }

        val paramsChangesEmitter =
            Observable.merge<PartialState>(
                listOf(
                    languagesEmitter,
                    textEmitter,
                    languageFromEmitter,
                    languageToEmitter
                )
            )

        val translationEmitter = translation
            .map { IdleTranslationViewState() }

        val viewStateEmitter =
            Observable.merge<TranslationViewState>(
                listOf(
                    translationEmitter,
                    languagesEmitter,
                    textEmitter,
                    languageFromEmitter,
                    languageToEmitter
                )
            )
                .scan<TranslationViewState>(IdleTranslationViewState()) { old, new ->
                    new.combineWith(old)
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
            .map<TranslationViewState> {
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