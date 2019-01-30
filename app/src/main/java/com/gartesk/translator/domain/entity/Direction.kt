package com.gartesk.translator.domain.entity

data class Direction(val from: Language, val to: Language)

val Translation.direction: Direction
    get() = Direction(from.language, to.language)