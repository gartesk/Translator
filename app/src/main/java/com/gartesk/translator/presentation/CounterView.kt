package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Text
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface CounterView : MvpView {
    fun counterUpdateIntent(): Observable<Text>
    fun render(viewState: CounterViewState)
}