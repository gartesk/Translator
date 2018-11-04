package com.gartesk.translator.domain.entity

data class Language(val code: String? = null) {

    companion object {
        val UNKNOWN_LANGUAGE = Language()
    }

    val isUnknown: Boolean = code == null
}