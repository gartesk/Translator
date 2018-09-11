package com.gartesk.translator.presentation

sealed class TranslationViewState(val query: String)

class EmptyTranslationViewState(query: String) : TranslationViewState(query)
class LoadingTranslationViewState(query: String) : TranslationViewState(query)
class ResultTranslationViewState(query: String, val result: String) : TranslationViewState(query)
