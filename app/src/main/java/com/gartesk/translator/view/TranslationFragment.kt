package com.gartesk.translator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gartesk.translator.R
import com.gartesk.translator.TranslatorApplication
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

    override fun createPresenter(): TranslationPresenter =
        TranslationPresenter(
            (requireActivity().application as TranslatorApplication)
                .commandFactory.createTranslateStringCommand()
        )

    override fun translationIntent(): Observable<String> =
        RxView.clicks(translateButton)
            .mergeWith(Observable.just(Unit))
            .debounce(1, TimeUnit.SECONDS)
            .map { translatingInput.text.toString() }

    override fun cancellationIntent(): Observable<Unit> =
        RxView.clicks(cancelButton)
            .debounce(1, TimeUnit.SECONDS)
            .map { Unit }

    override fun render(viewState: TranslationViewState) {
        when (viewState) {
            is EmptyTranslationViewState -> renderEmptyState(viewState)
            is LoadingTranslationViewState -> renderLoadingState(viewState)
            is ResultTranslationViewState -> renderResultState(viewState)
        }
    }

    private fun renderEmptyState(viewState: EmptyTranslationViewState) {
        translatingProgress.visibility = View.GONE
        translateButton.isEnabled = true
        cancelButton.visibility = View.GONE
        translatingInput.isEnabled = true
        translatedText.text = ""
    }

    private fun renderLoadingState(viewState: LoadingTranslationViewState) {
        translatingProgress.visibility = View.VISIBLE
        translateButton.isEnabled = false
        cancelButton.visibility = View.VISIBLE
        translatingInput.isEnabled = false
        translatedText.text = ""
    }

    private fun renderResultState(viewState: ResultTranslationViewState) {
        translatingProgress.visibility = View.GONE
        translateButton.isEnabled = true
        cancelButton.visibility = View.GONE
        translatingInput.isEnabled = true
        translatedText.text = viewState.result
    }
}
