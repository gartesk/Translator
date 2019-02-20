package com.gartesk.translator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gartesk.translator.R
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.*
import com.gartesk.translator.view.core.DelegatedMviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_translation.*
import java.util.concurrent.TimeUnit

class TranslationFragment : DelegatedMviFragment<TranslationView, TranslationPresenter>(),
    TranslationView {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_translation, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        registerDelegatingView(DelegatingLanguagesView(this))
        super.onCreate(savedInstanceState)
    }

    override fun createPresenter(): TranslationPresenter {
        val commandFactory = (requireActivity().application as TranslatorApplication)
            .commandFactory
        return TranslationPresenter(commandFactory.createGetTranslationCommand())
    }

    override fun translationIntent(): Observable<Pair<Text, Language>> =
        RxView.clicks(translateButton)
            .debounce(1, TimeUnit.SECONDS)
            .map {
                val contentFrom = translatingInput.text.toString()
                val selectedDirection = directionSelection.getSelectedDirection()
                Text(contentFrom, selectedDirection.from) to selectedDirection.to
            }

    override fun cancellationIntent(): Observable<Unit> =
        RxView.clicks(cancelButton)
            .debounce(1, TimeUnit.SECONDS)
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
        translatingProgress.visibility = View.GONE
        translateButton.isEnabled = true
        cancelButton.visibility = View.GONE
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
            counterText.visibility = View.VISIBLE
        } else {
            counterText.visibility = View.GONE
        }
        translationResultCard.visibility = if (viewState.textTo.content.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun renderLoadingState() {
        translatingProgress.visibility = View.VISIBLE
        translateButton.isEnabled = false
        cancelButton.visibility = View.VISIBLE
        translatingInput.isEnabled = false
        translatedText.text = ""
        translatingInputLayout.error = null
        directionSelection.isEnabled = false
        counterText.text = ""
        counterText.visibility = View.GONE
        translationResultCard.visibility = View.GONE
    }

    private fun renderErrorState(viewState: ErrorTranslationViewState) {
        translatingProgress.visibility = View.GONE
        translateButton.isEnabled = true
        cancelButton.visibility = View.GONE
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
        counterText.visibility = View.GONE
        translationResultCard.visibility = View.GONE
    }
}
