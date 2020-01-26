package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.CounterRepository
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class GetTranslationCommand(
    private val translationRepository: TranslationRepository,
    private val counterRepository: CounterRepository
) {

    fun execute(textFrom: Text, languageTo: Language): Single<Translation> =
        translationRepository.translate(textFrom, languageTo)
            .flatMap { textTo ->
                counterRepository.increment(textFrom, languageTo)
                    .andThen(counterRepository.get(textFrom, languageTo))
                    .map { counter -> Translation(textFrom, textTo, counter) }
            }
            .subscribeOn(Schedulers.io())
}