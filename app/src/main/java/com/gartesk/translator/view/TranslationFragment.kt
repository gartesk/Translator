package com.gartesk.translator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

class TranslationFragment : DelegatedMviFragment<TranslationView, TranslationPresenter>(), TranslationView {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_translation, container, false)

    lateinit var languageFromAdapter: LanguagesAdapter
    lateinit var languageToAdapter: LanguagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        registerDelegatingView(DelegatingLanguagesView(this))
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        languageFromAdapter =
                LanguagesAdapter(requireContext(), R.layout.item_language, R.id.languageName)
        languageToAdapter =
                LanguagesAdapter(requireContext(), R.layout.item_language, R.id.languageName)
        languageFromSpinner.adapter = languageFromAdapter
        languageToSpinner.adapter = languageToAdapter
    }

    override fun createPresenter(): TranslationPresenter {
        val commandFactory = (requireActivity().application as TranslatorApplication)
            .commandFactory
        return TranslationPresenter(
            commandFactory.createTranslateTextToLanguageCommand()
        )
    }

    override fun translationIntent(): Observable<Pair<Text, Language>> =
        RxView.clicks(translateButton)
            .debounce(1, TimeUnit.SECONDS)
            .map {
                val contentFrom = translatingInput.text.toString()
                val languageFromPosition = languageFromSpinner.selectedItemPosition
                val languageFrom = if (languageFromPosition != AdapterView.INVALID_POSITION) {
                    languageFromAdapter.objects[languageFromPosition]
                } else {
                    Language.UNKNOWN_LANGUAGE
                }
                val languageToPosition = languageToSpinner.selectedItemPosition
                val languageTo = if (languageToPosition != AdapterView.INVALID_POSITION) {
                    languageToAdapter.objects[languageToPosition]
                } else {
                    Language.UNKNOWN_LANGUAGE
                }
                Text(contentFrom, languageFrom) to languageTo
            }

    override fun cancellationIntent(): Observable<Unit> =
        RxView.clicks(cancelButton)
            .debounce(1, TimeUnit.SECONDS)
            .map { Unit }

    override fun render(viewState: TranslationViewState) {
        renderCommonState(viewState)
        when (viewState) {
            is IdleTranslationViewState -> renderIdleState(viewState)
            is LoadingTranslationViewState -> renderLoadingState(viewState)
            is ErrorTranslationViewState -> renderErrorState(viewState)
        }
    }

    private fun renderCommonState(viewState: TranslationViewState) {
        translatingInput.setText(viewState.textFrom.content)
        val languageFromIndex = languageFromAdapter.objects.indexOf(viewState.textFrom.language)
        if (languageFromIndex != AdapterView.INVALID_POSITION) {
            languageFromSpinner.setSelection(languageFromIndex)
        }
        val languageToIndex = languageToAdapter.objects.indexOf(viewState.textTo.language)
        if (languageToIndex != AdapterView.INVALID_POSITION) {
            languageToSpinner.setSelection(languageToIndex)
        }
    }

    private fun renderIdleState(viewState: IdleTranslationViewState) {
        translatingProgress.visibility = View.GONE
        translateButton.isEnabled = true
        cancelButton.visibility = View.GONE
        translatingInput.isEnabled = true
        translatedText.text = viewState.textTo.content
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
