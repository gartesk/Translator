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
import io.reactivex.functions.BiFunction

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
            Observable.merge<PartialStateVisitor>(
                listOf(
                    languagesEmitter,
                    textEmitter,
                    languageFromEmitter,
                    languageToEmitter
                )
            )
                .scan(PartialState()) { partialState, visitor ->
                    partialState.apply(visitor)
                }

        val viewStateEmitter = Observable.combineLatest(
            paramsChangesEmitter,
            translation,
            BiFunction<PartialState, Unit, TranslationViewState> { partialState, _ ->
                IdleTranslationViewState(
                    partialState.textFrom,
                    Text(language = partialState.languageTo),
                    partialState.languages
                )
            })
            .concatMap { translate(it, cancellation) }
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(viewStateEmitter, TranslationView::render)
    }

    private fun translate(
        initialViewState: TranslationViewState,
        cancellation: Observable<Unit>
    ): Observable<TranslationViewState> {
        if (initialViewState.textFrom.content.isEmpty()) {
            return Observable.just(
                ErrorTranslationViewState(
                    initialViewState.textFrom,
                    initialViewState.textTo,
                    ErrorType.EMPTY_TEXT,
                    initialViewState.languages
                )
            )
        } else if (initialViewState.textTo.language == Language.UNKNOWN_LANGUAGE) {
            return Observable.just(
                ErrorTranslationViewState(
                    initialViewState.textFrom,
                    initialViewState.textTo,
                    ErrorType.TARGET_LANGUAGE,
                    initialViewState.languages
                )
            )
        }
        val argument = initialViewState.textFrom to initialViewState.textTo.language
        return translateTextToLanguageCommand.execute(argument)
            .toObservable()
            .takeUntil(cancellation)
            .map<TranslationViewState> {
                IdleTranslationViewState(
                    initialViewState.textFrom,
                    initialViewState.textTo,
                    initialViewState.languages
                )
            }
            .defaultIfEmpty(
                IdleTranslationViewState(
                    initialViewState.textFrom,
                    initialViewState.textTo,
                    initialViewState.languages
                )
            )
            .startWith(
                LoadingTranslationViewState(
                    initialViewState.textFrom,
                    initialViewState.textTo,
                    initialViewState.languages
                )
            )
            .onErrorReturn {
                ErrorTranslationViewState(
                    initialViewState.textFrom,
                    initialViewState.textTo,
                    ErrorType.CONNECTION,
                    initialViewState.languages
                )
            }
    }
}