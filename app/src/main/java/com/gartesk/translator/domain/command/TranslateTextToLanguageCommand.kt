package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.CounterRepository
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TranslateTextToLanguageCommand(
    private val translationRepository: TranslationRepository,
    private val counterRepository: CounterRepository
) : SingleCommand<Pair<Text, Language>, Translation> {

    override fun execute(argument: Pair<Text, Language>): Single<Translation> =
        translationRepository.translate(argument.first, argument.second)
            .map { Translation(argument.first, it) }
            .flatMap {
                counterRepository.increment(it.from)
                    .toSingleDefault(it)
            }
            .subscribeOn(Schedulers.io())
}