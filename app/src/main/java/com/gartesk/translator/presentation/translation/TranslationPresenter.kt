package com.gartesk.translator.presentation.translation

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.GetDefaultLanguageCommand
import com.gartesk.translator.domain.command.GetTranslationCommand
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.translation.ErrorTranslationViewState.ErrorType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class TranslationPresenter(
	private val getTranslationCommand: GetTranslationCommand,
	private val getDefaultLanguageCommand: GetDefaultLanguageCommand
) : MviBasePresenter<TranslationView, TranslationViewState>() {

	private val cancellationRelay = PublishSubject.create<Unit>()
	private var cancellationDisposable: Disposable? = null

	override fun bindIntents() {
		val cancellation = intent(TranslationView::cancellationIntent)
		val translation = intent(TranslationView::translationIntent)
		val languageSelection = intent(TranslationView::languageSelectionIntent)
			.startWith(
				getDefaultLanguageCommand.execute()
					.toObservable()
					.onErrorReturnItem(Language.UNKNOWN_LANGUAGE)
			)

		cancellationDisposable = cancellation.subscribe { cancellationRelay.onNext(Unit) }

		val viewStateEmitter = translation
			.concatMap { textFrom ->
				languageSelection.flatMap { actualLanguageTo -> translate(textFrom, actualLanguageTo) }
			}
			.observeOn(AndroidSchedulers.mainThread())

		subscribeViewState(viewStateEmitter, TranslationView::render)
	}

	private fun translate(
		textFrom: String,
		languageTo: Language
	): Observable<TranslationViewState> {
		if (textFrom.isEmpty()) {
			return Observable.just(
				ErrorTranslationViewState(
					Text(content = textFrom),
					Text(language = languageTo),
					ErrorType.EMPTY_TEXT
				)
			)
		}

		return getTranslationCommand.execute(textFrom, languageTo)
			.toObservable()
			.takeUntil(cancellationRelay)
			.map<TranslationViewState> { IdleTranslationViewState(it.from, it.to, it.counter) }
			.defaultIfEmpty(IdleTranslationViewState(Text(content = textFrom), Text(language = languageTo)))
			.startWith(LoadingTranslationViewState(Text(content = textFrom), Text(language = languageTo)))
			.onErrorReturn {
				ErrorTranslationViewState(
					Text(content = textFrom),
					Text(language = languageTo),
					ErrorType.CONNECTION
				)
			}
	}

	override fun unbindIntents() {
		super.unbindIntents()
		cancellationDisposable?.dispose()
	}
}