package com.gartesk.translator.view

import android.content.Context
import android.widget.ArrayAdapter
import com.gartesk.translator.domain.entity.Language

class LanguagesAdapter(
    context: Context,
    resource: Int,
    textViewResourceId: Int
) : ArrayAdapter<Language>(context, resource, textViewResourceId) {

    var objects: Array<Language> = emptyArray()
        set(value) {
            clear()
            addAll(*value)
        }
}