package com.gartesk.translator.presentation

import com.gartesk.translator.domain.entity.Counter

sealed class CounterViewState

object EmptyCounterViewState : CounterViewState()

class SuccessCounterViewState(val counter: Counter) : CounterViewState()

