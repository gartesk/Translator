package com.gartesk.translator.presentation

import com.gartesk.translator.domain.command.ObservableCommand
import com.gartesk.translator.domain.entity.Language
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers

class LanguagesPresenter(
    private val listLanguagesCommand: ObservableCommand<Unit, List<Language>>
) : MviBasePresenter<LanguagesView, LanguagesViewState>() {

    override fun bindIntents() {
        val languagesEmitter = listLanguagesCommand.execute(Unit)
            .map { LanguagesViewState(it) }
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(languagesEmitter, LanguagesView::render)
    }
}