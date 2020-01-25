package com.gartesk.translator.presentation

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.SingleCommand
import com.gartesk.translator.domain.entity.Direction
import io.reactivex.android.schedulers.AndroidSchedulers

class LanguagesPresenter(
    private val getDirectionsCommand: SingleCommand<Unit, List<Direction>>
) : MviBasePresenter<LanguagesView, LanguagesViewState>() {

    override fun bindIntents() {
        val languagesEmitter = getDirectionsCommand.execute(Unit)
            .toObservable()
            .map { LanguagesViewState(it) }
            .onErrorReturn { LanguagesViewState(listOf(Direction.UNKNOWN_DIRECTION)) }
            .startWith(LanguagesViewState(listOf(Direction.UNKNOWN_DIRECTION)))
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(languagesEmitter, LanguagesView::render)
    }
}