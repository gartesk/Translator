package com.gartesk.translator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gartesk.translator.R
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.presentation.*
//import com.google.android.material.textfield.TextInputEditText
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.widget.RxTextView
//import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_translation.*

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
        RxTextView.textChanges(translationInput).map { it.toString() }

    override fun render(viewState: TranslationViewState) {
        when (viewState) {
            is EmptyTranslationViewState -> renderEmptyState(viewState)
            is LoadingTranslationViewState -> renderLoadingState(viewState)
            is ResultTranslationViewState -> renderResultState(viewState)
        }
    }

    private fun renderEmptyState(viewState: EmptyTranslationViewState) {
        testText.text = "Empty"
    }

    private fun renderLoadingState(viewState: LoadingTranslationViewState) {
        testText.text = "Loading"
    }

    private fun renderResultState(viewState: ResultTranslationViewState) {
        testText.text = "Result"
    }
}
