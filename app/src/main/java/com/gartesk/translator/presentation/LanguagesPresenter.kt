package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.SingleCommand
import com.gartesk.translator.domain.entity.Direction
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers

class LanguagesPresenter(
    private val getDirectionsCommand: SingleCommand<Unit, List<Direction>>
) : MviBasePresenter<LanguagesView, LanguagesViewState>() {

    override fun bindIntents() {
        val languagesEmitter = getDirectionsCommand.execute(Unit)
            .toObservable()
            .map { LanguagesViewState(it) }
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(languagesEmitter, LanguagesView::render)
    }
}