package com.gartesk.translator.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.repository.CounterRepository
import io.reactivex.Completable
import io.reactivex.Single

class PrefsCounterRepository(context: Context) : CounterRepository {

	companion object {
		private const val PREFS_NAME = "prefs.counter"
	}

	private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

	override fun increment(textFrom: Text, languageTo: Language): Completable =
		Completable.fromAction {
			val prefsKey = getPrefsKey(textFrom, languageTo)
			val previousValue = prefs.getInt(prefsKey, 0)
			val updatedValue = previousValue + 1
			prefs.edit().putInt(prefsKey, updatedValue).apply()
		}

	private fun getPrefsKey(textFrom: Text, languageTo: Language): String =
		"$PREFS_NAME.${textFrom.content}.${textFrom.language.code}.${languageTo.code}"

	override fun get(textFrom: Text, languageTo: Language): Single<Int> =
		Single.fromCallable { prefs.getInt(getPrefsKey(textFrom, languageTo), 0) }
}