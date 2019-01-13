package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.ObservableCommand
import com.gartesk.translator.domain.entity.Counter
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable

class CounterPresenter(
    private val getCounterUpdatesCommand: ObservableCommand<Text, Counter>
) : MviBasePresenter<CounterView, CounterViewState>() {

    override fun bindIntents() {
        val viewStateEmitter = intent(CounterView::counterUpdateIntent)
            .concatMap { getCounter(it) }

        subscribeViewState(viewStateEmitter, CounterView::render)
    }

    private fun getCounter(text: Text): Observable<CounterViewState> {
        if (text.content.isEmpty() || text.language == Language.UNKNOWN_LANGUAGE) {
            return Observable.just(EmptyCounterViewState)
        }
        return getCounterUpdatesCommand.execute(text)
            .map<CounterViewState> { SuccessCounterViewState(it) }
            .startWith(EmptyCounterViewState)
            .onErrorReturn { EmptyCounterViewState }
    }
}