package com.gartesk.translator.data

import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Maybe

class MockTranslationRepository : TranslationRepository {
    override fun translate(string: String): Maybe<String> =
            Maybe.just("Krappa-$string")
}