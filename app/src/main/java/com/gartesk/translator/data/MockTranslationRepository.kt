package com.gartesk.translator.data

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Maybe
import java.util.concurrent.TimeUnit

class MockTranslationRepository : TranslationRepository {
    override fun translate(text: Text, targetLanguage: Language): Maybe<Text> =
            Maybe.just(
                Text(
                    content = "Krappa-${text.content}-(${text.language}-$targetLanguage)",
                    language = targetLanguage
                )
            )
                .delay(10, TimeUnit.SECONDS)
}