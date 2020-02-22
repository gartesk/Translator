package com.gartesk.translator.view.translation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.translation.*
import com.gartesk.translator.view.core.DelegatedMviFragment
import com.gartesk.translator.view.getCommandFactory
import com.gartesk.translator.view.translation.languages.DelegatingLanguagesView
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_translation.*

class TranslationFragment : DelegatedMviFragment<TranslationView, TranslationPresenter>(),
	TranslationView {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_translation, container, false)

	override fun onCreate(savedInstanceState: Bundle?) {
		registerDelegatingView(
			DelegatingLanguagesView(
				this
			)
		)
		super.onCreate(savedInstanceState)
	}

	override fun createPresenter(): TranslationPresenter =
		TranslationPresenter(getCommandFactory().createGetTranslationCommand())

	override fun translationIntent(): Observable<Pair<Text, Language>> =
		translateButton.clicks()
			.map {
				val contentFrom = translatingInput.text.toString()
				val selectedDirection = directionSelection.getSelectedDirection()
				Text(contentFrom, selectedDirection.from) to selectedDirection.to
			}

	override fun cancellationIntent(): Observable<Unit> =
		cancelButton.clicks()
			.map { Unit }

	override fun render(viewState: TranslationViewState) {
		renderCommonState(viewState)
		when (viewState) {
			is IdleTranslationViewState -> renderIdleState(viewState)
			is LoadingTranslationViewState -> renderLoadingState()
			is ErrorTranslationViewState -> renderErrorState(viewState)
		}
	}

	private fun renderCommonState(viewState: TranslationViewState) {
		translatingInput.setText(viewState.textFrom.content)
		directionSelection.selectDirection(viewState.textFrom.language, viewState.textTo.language)
	}

	private fun renderIdleState(viewState: IdleTranslationViewState) {
		translatingProgress.visibility = GONE
		translateButton.isEnabled = true
		cancelButton.visibility = GONE
		translatingInput.isEnabled = true
		translatedText.text = viewState.textTo.content
		translatingInputLayout.error = null
		directionSelection.isEnabled = true
		if (viewState.counter != null) {
			counterText.text = resources.getQuantityString(
				R.plurals.counter_text_format,
				viewState.counter,
				viewState.counter
			)
			counterText.visibility = VISIBLE
		} else {
			counterText.visibility = GONE
		}
		translationResultCard.visibility = if (viewState.textTo.content.isEmpty()) GONE else VISIBLE
	}

	private fun renderLoadingState() {
		translatingProgress.visibility = VISIBLE
		translateButton.isEnabled = false
		cancelButton.visibility = VISIBLE
		translatingInput.isEnabled = false
		translatedText.text = ""
		translatingInputLayout.error = null
		directionSelection.isEnabled = false
		counterText.text = ""
		counterText.visibility = GONE
		translationResultCard.visibility = GONE
	}

	private fun renderErrorState(viewState: ErrorTranslationViewState) {
		translatingProgress.visibility = GONE
		translateButton.isEnabled = true
		cancelButton.visibility = GONE
		translatingInput.isEnabled = true
		translatedText.text = ""
		translatingInputLayout.error = when (viewState.error) {
			ErrorTranslationViewState.ErrorType.CONNECTION ->
				getString(R.string.translation_error_connection)
			ErrorTranslationViewState.ErrorType.EMPTY_TEXT ->
				getString(R.string.translation_error_empty_text)
			ErrorTranslationViewState.ErrorType.TARGET_LANGUAGE ->
				getString(R.string.translation_error_target_language)
		}
		directionSelection.isEnabled = true
		counterText.text = ""
		counterText.visibility = GONE
		translationResultCard.visibility = GONE
	}
}
