package com.gartesk.translator.domain.entity

data class Language(val value: String? = null) {

    companion object {
        val UNKNOWN_LANGUAGE = Language()
    }

    val isUnknown: Boolean = value == null
}