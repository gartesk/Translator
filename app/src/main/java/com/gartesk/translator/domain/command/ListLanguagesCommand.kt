package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ListLanguagesCommand(
    private val translationRepository: TranslationRepository
) : SingleCommand<Unit, List<Language>> {

    override fun execute(argument: Unit): Single<List<Language>> =
        translationRepository.listLanguages(Language("en"))
            .subscribeOn(Schedulers.io())
}