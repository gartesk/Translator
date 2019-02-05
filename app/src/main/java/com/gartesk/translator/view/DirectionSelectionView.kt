package com.gartesk.translator.view

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Direction
import com.gartesk.translator.domain.entity.Language
import kotlinx.android.synthetic.main.view_direction_selection.view.*

class DirectionSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val fromAdapter: LanguagesAdapter
    private val toAdapter: LanguagesAdapter

    init {
        inflate(context, R.layout.view_direction_selection, this)
        fromAdapter = LanguagesAdapter(context, R.layout.item_language, R.id.languageName)
        toAdapter = LanguagesAdapter(context, R.layout.item_language, R.id.languageName)
    }

    private var directions: List<Direction> = listOf(
        Direction(Language.UNKNOWN_LANGUAGE, Language.UNKNOWN_LANGUAGE)
    )

    fun setDirections(directions: List<Direction>) {
        val selectedDirection = getSelectedDirection()
        val languagesFrom = directions.map { it.from }.distinct()
        val partialDirectionsFrom = languagesFrom.map { Direction(it, Language.UNKNOWN_LANGUAGE) }
        val languagesTo = directions.map { it.to }.distinct()
        val partialDirectionsTo = languagesTo.map { Direction(Language.UNKNOWN_LANGUAGE, it) }
        this.directions = listOf(Direction(Language.UNKNOWN_LANGUAGE, Language.UNKNOWN_LANGUAGE)) +
                directions + partialDirectionsFrom + partialDirectionsTo
        fromAdapter.objects = arrayOf(Language.UNKNOWN_LANGUAGE) + languagesFrom
        toAdapter.objects = arrayOf(Language.UNKNOWN_LANGUAGE) + languagesTo
        selectDirection(selectedDirection.from, selectedDirection.to)
    }

    fun selectDirection(languageFrom: Language, languageTo: Language) {
        val fromIndex = fromAdapter.objects.indexOf(languageFrom)
        val toIndex = toAdapter.objects.indexOf(languageTo)
        var fromIndexToSelect = fromIndex
        var toIndexToSelect = toIndex
        if (fromIndex != AdapterView.INVALID_POSITION) {
            if (!directions.contains(Direction(languageFrom, languageTo))) {
                toIndexToSelect = 0
            }
        } else {
            fromIndexToSelect = 0
        }
        if (toIndex == AdapterView.INVALID_POSITION) {
            toIndexToSelect = 0
        }
        languageFromSpinner.setSelection(fromIndexToSelect)
        languageToSpinner.setSelection(toIndexToSelect)
    }

    fun getSelectedDirection(): Direction {
        val selectedLanguageFrom = languageFromSpinner.selectedItem as Language
        val selectedLanguageTo = languageToSpinner.selectedItem as Language
        return Direction(selectedLanguageFrom, selectedLanguageTo)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        languageFromSpinner.isEnabled = enabled
        languageToSpinner.isEnabled = enabled
    }
}

private class LanguagesAdapter(
    context: Context,
    resource: Int,
    textViewResourceId: Int
) : ArrayAdapter<Language>(context, resource, textViewResourceId) {

    var objects: Array<Language> = emptyArray()
        set(value) {
            if (value.contentEquals(field)) {
                return
            }
            field = value
            clear()
            addAll(*value)
        }
}