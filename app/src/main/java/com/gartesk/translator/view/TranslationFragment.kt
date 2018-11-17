package com.gartesk.translator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gartesk.translator.R
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.presentation.*
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_translation.*
import java.util.concurrent.TimeUnit

class TranslationFragment : MviFragment<TranslationView, TranslationPresenter>(), TranslationView {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_translation, container, false)

    private lateinit var languageFromAdapter: LanguagesAdapter
    private lateinit var languageToAdapter: LanguagesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        languageFromAdapter = LanguagesAdapter(requireContext(), R.layout.item_language, R.id.languageName)
        languageToAdapter = LanguagesAdapter(requireContext(), R.layout.item_language, R.id.languageName)
        languageFromSpinner.adapter = languageFromAdapter
        languageToSpinner.adapter = languageToAdapter
    }

    override fun createPresenter(): TranslationPresenter {
        val commandFactory = (requireActivity().application as TranslatorApplication)
            .commandFactory
        return TranslationPresenter(
            commandFactory.createTranslateTextToLanguageCommand(),
            commandFactory.createListLanguagesCommand()
        )
    }

    override fun translationIntent(): Observable<Unit> =
        RxView.clicks(translateButton)
            .debounce(1, TimeUnit.SECONDS)
            .map { Unit }

    override fun cancellationIntent(): Observable<Unit> =
        RxView.clicks(cancelButton)
            .debounce(1, TimeUnit.SECONDS)
            .map { Unit }

    override fun textIntent(): Observable<String> =
        RxTextView.textChanges(translatingInput)
            .map { it.toString() }

    override fun languageFromIntent(): Observable<Language> =
        RxAdapterView.itemSelections(languageFromSpinner)
            .map { languageFromAdapter.objects[it] }

    override fun languageToIntent(): Observable<Language> =
        RxAdapterView.itemSelections(languageToSpinner)
            .map { languageToAdapter.objects[it] }

    override fun render(viewState: TranslationViewState) {
        renderCommonState(viewState)
        when (viewState) {
            is IdleTranslationViewState -> renderEmptyState(viewState)
            is LoadingTranslationViewState -> renderLoadingState(viewState)
            is ErrorTranslationViewState -> renderErrorState(viewState)
        }
    }

    private fun renderCommonState(viewState: TranslationViewState) {
        translatingInput.setText(viewState.textFrom.content)
        languageFromAdapter.objects = viewState.languages.toTypedArray()
        languageToAdapter.objects = viewState.languages.toTypedArray()
        val languageFromIndex = viewState.languages.indexOf(viewState.textFrom.language)
        languageFromSpinner.setSelection(languageFromIndex)
        val languageToIndex = viewState.languages.indexOf(viewState.textTo.language)
        languageToSpinner.setSelection(languageToIndex)
    }

    private fun renderEmptyState(viewState: IdleTranslationViewState) {
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
