package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

class TranslateTextToLanguageCommand(
    private val translationRepository: TranslationRepository
): MaybeCommand<Pair<Text, Language>, Translation> {

    override fun execute(argument: Pair<Text, Language>): Maybe<Translation> =
            translationRepository.translate(argument.first, argument.second)
                .map { Translation(argument.first, it) }
                .subscribeOn(Schedulers.io())
}