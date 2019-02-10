package com.gartesk.translator.data

import com.gartesk.translator.domain.entity.Direction
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Single

class MockTranslationRepository : TranslationRepository {

    override fun translate(text: Text, targetLanguage: Language): Single<Text> =
//        Single.just(Text(content = "${text.content}-${text.language.code}-${targetLanguage.code}"))
        Single.error(RuntimeException())

    override fun getDirections(): Single<List<Direction>> =
//        Single.just(
//            listOf(
//                Direction(Language("ru"), Language("en")),
//                Direction(Language("ru"), Language("fr")),
//                Direction(Language("en"), Language("ru")),
//                Direction(Language("en"), Language("fr")),
//                Direction(Language("fr"), Language("en")),
//                Direction(Language("fr"), Language("ru"))
//            )
//        )
        Single.error(RuntimeException())
}