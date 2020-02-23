package com.gartesk.translator.domain.entity

data class Stat(val from: Text, val counters: List<Counter>) {

	data class Counter(val language: Language, val value: Int)
}

val Stat.totalCounter: Int
	get() = counters.sumBy { it.value }