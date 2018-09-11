package com.gartesk.translator.domain.repository

import io.reactivex.Maybe

interface TranslationRepository {
    fun translate(string: String): Maybe<String>
}