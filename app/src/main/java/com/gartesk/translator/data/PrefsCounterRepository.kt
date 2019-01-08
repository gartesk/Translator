package com.gartesk.translator.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.gartesk.translator.domain.entity.Counter
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.repository.CounterRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class PrefsCounterRepository(context: Context) : CounterRepository {

    companion object {
        private const val PREFS_NAME = "prefs.counter"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    private val subject: Subject<Counter> = BehaviorSubject.create()
    private var currentKey: String? = null

    override fun increment(text: Text): Completable =
        Completable.fromAction {
            val previousValue = prefs.getInt(text.prefsKey, 0)
            val updatedValue = previousValue + 1
            prefs.edit().putInt(text.prefsKey, updatedValue).apply()
            if (text.prefsKey == currentKey) {
                subject.onNext(Counter(text, updatedValue))
                subject.onComplete()
            }
        }

    private val Text.prefsKey: String
        get() = "$PREFS_NAME.$content.${language.code}"

    override fun getUpdates(text: Text): Observable<Counter> {
        if (text.prefsKey != currentKey) {
            val currentValue = prefs.getInt(text.prefsKey, 0)
            currentKey = text.prefsKey
            subject.onNext(Counter(text, currentValue))
        }
        return subject.hide()
    }
}