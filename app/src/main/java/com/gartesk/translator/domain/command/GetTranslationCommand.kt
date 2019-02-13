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
) : SingleCommand<Pair<Text, Language>, Translation> {

    override fun execute(argument: Pair<Text, Language>): Single<Translation> =
        translationRepository.translate(argument.first, argument.second)
            .flatMap { textTo ->
                counterRepository.increment(argument.first, argument.second)
                    .andThen(counterRepository.get(argument.first, argument.second))
                    .map { counter -> Translation(argument.first, textTo, counter) }
            }
            .subscribeOn(Schedulers.io())
}