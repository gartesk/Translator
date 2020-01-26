package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Direction
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class GetDirectionsCommand(
    private val translationRepository: TranslationRepository
) {

    fun execute(): Single<List<Direction>> =
        translationRepository.getDirections()
            .subscribeOn(Schedulers.io())
}