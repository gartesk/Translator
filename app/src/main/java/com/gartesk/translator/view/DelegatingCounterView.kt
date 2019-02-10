package com.gartesk.translator.view

import android.view.View
import com.gartesk.translator.R
import com.gartesk.translator.TranslatorApplication
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.*
import com.gartesk.translator.view.core.DelegatingMviView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_translation.*

class DelegatingCounterView(
    private val translationFragment: TranslationFragment
) : DelegatingMviView<CounterView, CounterPresenter>(translationFragment), CounterView {

    override fun createPresenter(): CounterPresenter {
        val commandFactory =
            (delegatedFragment.requireActivity().application as TranslatorApplication).commandFactory
        return CounterPresenter(commandFactory.createGetCounterUpdatesCommand())
    }

    override fun counterUpdateIntent(): Observable<Text> =
        translationFragment.translationSuccessObservable

    override fun render(viewState: CounterViewState) {
        when (viewState) {
            EmptyCounterViewState -> renderEmptyCounter()
            is SuccessCounterViewState -> renderCounter(viewState)
        }
    }

    private fun renderEmptyCounter() {
        translationFragment.counterText.text = ""
        translationFragment.counterText.visibility = View.GONE
    }

    private fun renderCounter(viewState: SuccessCounterViewState) {
        translationFragment.counterText.text = translationFragment.resources.getQuantityString(
            R.plurals.counter_text_format,
            viewState.counter.count,
            viewState.counter.count
        )
    }
}