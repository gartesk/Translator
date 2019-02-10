package com.gartesk.translator.domain.entity

data class Direction(val from: Language, val to: Language) {

    companion object {
        val UNKNOWN_DIRECTION = Direction(Language.UNKNOWN_LANGUAGE, Language.UNKNOWN_LANGUAGE)
    }
}

val Translation.direction: Direction
    get() = Direction(from.language, to.language)