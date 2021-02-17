package com.gartesk.translator.domain.entity

data class Translation(val from: Text, val to: Text)

data class CountedTranslation(val from: Text, val to: Text, val counter: Int)