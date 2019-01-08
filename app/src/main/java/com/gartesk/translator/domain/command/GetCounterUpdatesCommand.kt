package com.gartesk.translator.domain.command

import com.gartesk.translator.domain.entity.Counter
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.repository.CounterRepository
import io.reactivex.Observable

class GetCounterUpdatesCommand(
    private val counterRepository: CounterRepository
) : ObservableCommand<Text, Counter> {

    override fun execute(argument: Text): Observable<Counter> =
        counterRepository.getUpdates(argument)
}