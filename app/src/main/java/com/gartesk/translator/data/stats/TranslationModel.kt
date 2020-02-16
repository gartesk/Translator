package com.gartesk.translator.data.stats

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class TranslationModel(
	@Id var id: Long? = null,
	val textFrom: String,
	val languageFrom: String,
	val textTo: String,
	val languageTo: String,
	val counter: Int
)