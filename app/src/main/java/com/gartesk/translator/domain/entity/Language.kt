package com.gartesk.translator.domain.entity

data class Language(val code: String) {

	companion object {
		val UNKNOWN_LANGUAGE = Language(code = "UNKNOWN")
	}

	val isUnknown: Boolean = this == UNKNOWN_LANGUAGE
}