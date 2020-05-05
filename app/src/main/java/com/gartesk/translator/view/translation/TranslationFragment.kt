package com.gartesk.translator.view.translation

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import com.gartesk.mosbyx.mvi.MviFragment
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Direction
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.presentation.translation.*
import com.gartesk.translator.view.commandFactory
import com.gartesk.translator.view.navigator
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_translation.*

class TranslationFragment : MviFragment<TranslationView, TranslationPresenter>(),
	TranslationView {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_translation, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.common, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		if (item.itemId == R.id.about) {
			navigator.openAbout()
			true
		} else {
			super.onOptionsItemSelected(item)
		}

	override fun createPresenter(): TranslationPresenter =
		TranslationPresenter(
			commandFactory.createGetTranslationCommand(),
			commandFactory.createGetDefaultLanguageCommand()
		)

	override fun translationIntent(): Observable<Pair<Text, Language>> =
		translateButton.clicks()
			.map {
				val contentFrom = translatingInput.text.toString()
				val selectedDirection = languagesLayout.getSelectedDirection()
				Text(contentFrom, selectedDirection.from) to selectedDirection.to
			}
			.let {
				val initialTranslation = navigator.getTranslationArguments(arguments)
				if (initialTranslation != null) {
					it.startWith(initialTranslation)
				} else {
					it
				}
			}

	override fun cancellationIntent(): Observable<Unit> =
		cancelButton.clicks()
			.map { Unit }

	override fun render(viewState: TranslationViewState) {
		renderCommonState(viewState)
		when (viewState) {
			is IdleTranslationViewState -> renderIdleState(viewState)
			is LoadingTranslationViewState -> renderLoadingState()
			is ErrorTranslationViewState -> renderErrorState(viewState)
		}
	}

	private fun renderCommonState(viewState: TranslationViewState) {
		translatingInput.setText(viewState.textFrom.content)
		languagesLayout.setSelectedDirection(
			Direction(viewState.textFrom.language, viewState.textTo.language)
		)
	}

	private fun renderIdleState(viewState: IdleTranslationViewState) {
		translatingProgress.visibility = GONE
		translateButton.isEnabled = true
		cancelButton.visibility = GONE
		translatingInput.isEnabled = true
		translatedText.text = viewState.textTo.content
		translatingInputLayout.error = null
		languagesLayout.isEnabled = true
		if (viewState.counter != null) {
			counterText.text = resources.getQuantityString(
				R.plurals.counter_text_format,
				viewState.counter,
				viewState.counter
			)
			counterText.visibility = VISIBLE
		} else {
			counterText.visibility = GONE
		}
		translationResultCard.visibility = if (viewState.textTo.content.isEmpty()) GONE else VISIBLE
	}

	private fun renderLoadingState() {
		translatingProgress.visibility = VISIBLE
		translateButton.isEnabled = false
		cancelButton.visibility = VISIBLE
		translatingInput.isEnabled = false
		translatedText.text = ""
		translatingInputLayout.error = null
		languagesLayout.isEnabled = false
		counterText.text = ""
		counterText.visibility = GONE
		translationResultCard.visibility = GONE
	}

	private fun renderErrorState(viewState: ErrorTranslationViewState) {
		translatingProgress.visibility = GONE
		translateButton.isEnabled = true
		cancelButton.visibility = GONE
		translatingInput.isEnabled = true
		translatedText.text = ""
		translatingInputLayout.error = when (viewState.error) {
			ErrorTranslationViewState.ErrorType.CONNECTION ->
				getString(R.string.translation_error_connection)
			ErrorTranslationViewState.ErrorType.EMPTY_TEXT ->
				getString(R.string.translation_error_empty_text)
		}
		languagesLayout.isEnabled = true
		counterText.text = ""
		counterText.visibility = GONE
		translationResultCard.visibility = GONE
	}
}
