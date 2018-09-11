package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

class TranslateStringCommand(
    private val translationRepository: TranslationRepository
): MaybeCommand<String, String> {

    override fun execute(argument: String): Maybe<String> =
            translationRepository.translate(argument)
                .subscribeOn(Schedulers.io())
}