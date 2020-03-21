package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Direction
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import io.reactivex.Single

interface TranslationRepository {
	fun translate(textFrom: Text, languageTo: Language): Single<Pair<Language, Text>>

	fun getDirections(): Single<List<Direction>>
}