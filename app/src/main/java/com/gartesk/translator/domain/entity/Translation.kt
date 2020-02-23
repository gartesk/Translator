package com.gartesk.translator.domain.entity

data class Translation(val from: Text, val to: Text, val counter: Int)

fun Stat.toTranslation(textTo: String, languageTo: Language): Translation =
	Translation(
		from = from,
		to = Text(textTo, languageTo),
		counter = counters
			.firstOrNull { it.language == languageTo }
			?.value ?: 0
	)