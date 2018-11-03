package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import io.reactivex.Maybe

interface TranslationRepository {
    fun translate(text: Text, targetLanguage: Language): Maybe<Text>
}