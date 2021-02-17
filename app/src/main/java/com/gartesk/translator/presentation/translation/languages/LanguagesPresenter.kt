package com.gartesk.translator.presentation.translation.languages

import com.gartesk.mosbyx.mvi.MviBasePresenter
import com.gartesk.translator.domain.command.GetLanguagesCommand
import com.gartesk.translator.domain.entity.Language
import io.reactivex.android.schedulers.AndroidSchedulers

class LanguagesPresenter(
	private val getLanguagesCommand: GetLanguagesCommand
) : MviBasePresenter<LanguagesView, LanguagesViewState>() {

	override fun bindIntents() {
		val viewStateEmitter = getLanguagesCommand.execute()
			.toObservable()
			.onErrorReturn { listOf(Language.UNKNOWN_LANGUAGE) }
			.startWith(listOf(Language.UNKNOWN_LANGUAGE))
			.map { LanguagesViewState(it) }
			.observeOn(AndroidSchedulers.mainThread())

		subscribeViewState(viewStateEmitter, LanguagesView::render)
	}
}