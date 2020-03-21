package com.gartesk.translator.domain.entity

data class Direction(val from: Language, val to: Language) {

	companion object {
		val UNKNOWN_DIRECTION = Direction(Language.UNKNOWN_LANGUAGE, Language.UNKNOWN_LANGUAGE)
	}
}

val Translation.direction: Direction
	get() = Direction(from.language, to.language)

val Direction.reverted: Direction
	get() = Direction(to, from)

val List<Direction>.languagesFrom: List<Language>
	get() = map { it.from }
		.distinct()
		.filter { it != Language.UNKNOWN_LANGUAGE }
		.sortedBy { it.code }

val List<Direction>.languagesTo: List<Language>
	get() = map { it.to }
		.distinct()
		.filter { it != Language.UNKNOWN_LANGUAGE }
		.sortedBy { it.code }