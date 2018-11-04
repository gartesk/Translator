package com.gartesk.translator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.gartesk.translator.R
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.*
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_translation.*
import java.util.concurrent.TimeUnit

class TranslationFragment : MviFragment<TranslationView, TranslationPresenter>(), TranslationView {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_translation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val languages = arrayOf(Language.UNKNOWN_LANGUAGE, Language("en"), Language("ru"))
        languageFromSpinner.adapter = ArrayAdapter<Language>(requireContext(), R.layout.item_language, R.id.languageName, languages)
        languageToSpinner.adapter = ArrayAdapter<Language>(requireContext(), R.layout.item_language, R.id.languageName, languages)
    }

    override fun createPresenter(): TranslationPresenter {
        val commandFactory = (requireActivity().application as TranslatorApplication)
            .commandFactory
        return TranslationPresenter(
            commandFactory.createTranslateTextToLanguageCommand(),
            commandFactory.createListLanguagesCommand()
        )
    }

    override fun translationIntent(): Observable<Pair<Text, Language>> =
        RxView.clicks(translateButton)
            .debounce(1, TimeUnit.SECONDS)
            .map {
                val query = translatingInput.text.toString()
                val languageFrom = languageFromSpinner.selectedItem as Language
                val languageTo = languageToSpinner.selectedItem as Language
                Text(query, languageFrom) to languageTo
            }

    override fun cancellationIntent(): Observable<Unit> =
        RxView.clicks(cancelButton)
            .debounce(1, TimeUnit.SECONDS)
            .map { Unit }

    override fun render(viewState: TranslationViewState) {
        renderCommonState(viewState)
        when (viewState) {
            is EmptyTranslationViewState -> renderEmptyState(viewState)
            is LoadingTranslationViewState -> renderLoadingState(viewState)
            is ResultTranslationViewState -> renderResultState(viewState)
            is ErrorTranslationViewState -> renderErrorState(viewState)
        }
    }

    private fun renderCommonState(viewState: TranslationViewState) {
        translatingInput.setText(viewState.textFrom.content)
        //TODO: selection
    }

    private fun renderEmptyState(viewState: EmptyTranslationViewState) {
        translatingProgress.visibility = View.GONE
        translateButton.isEnabled = true
        cancelButton.visibility = View.GONE
        translatingInput.isEnabled = true
        translatedText.text = ""
        translatingInputLayout.error = null
        languageFromSpinner.isEnabled = true
        languageToSpinner.isEnabled = true
    }

    private fun renderLoadingState(viewState: LoadingTranslationViewState) {
        translatingProgress.visibility = View.VISIBLE
        translateButton.isEnabled = false
        cancelButton.visibility = View.VISIBLE
        translatingInput.isEnabled = false
        translatedText.text = ""
        translatingInputLayout.error = null
        languageFromSpinner.isEnabled = false
        languageToSpinner.isEnabled = false
    }

    private fun renderResultState(viewState: ResultTranslationViewState) {
        translatingProgress.visibility = View.GONE
        translateButton.isEnabled = true
        cancelButton.visibility = View.GONE
        translatingInput.isEnabled = true
        translatedText.text = viewState.result
        translatingInputLayout.error = null
        languageFromSpinner.isEnabled = true
        languageToSpinner.isEnabled = true
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
        languageFromSpinner.isEnabled = true
        languageToSpinner.isEnabled = true
    }
}
