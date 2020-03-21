package com.gartesk.translator.presentation.translation.languages

import com.gartesk.translator.domain.entity.Direction

data class LanguagesViewState(val directions: List<Direction>, val selectedDirection: Direction)