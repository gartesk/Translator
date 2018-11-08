package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ListLanguagesCommand(
    private val translationRepository: TranslationRepository
) : ObservableCommand<Unit, List<Language>> {

    override fun execute(argument: Unit): Observable<List<Language>> =
        translationRepository.listLanguages(Language("en"))
            .toObservable()
            .map { listOf(Language.UNKNOWN_LANGUAGE) + it }
            .startWith(listOf(Language.UNKNOWN_LANGUAGE))
            .subscribeOn(Schedulers.io())
}