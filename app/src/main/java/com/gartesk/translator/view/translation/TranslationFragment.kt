package com.gartesk.translator.view.translation

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import com.gartesk.mosbyx.mvi.MviFragment
import com.gartesk.translator.R
import com.gartesk.translator.databinding.FragmentTranslationBinding
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.presentation.translation.*
import com.gartesk.translator.view.commandFactory
import com.gartesk.translator.view.navigator
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable

class TranslationFragment : MviFragment<TranslationView, TranslationPresenter>(),
	TranslationView {

	private lateinit var binding: FragmentTranslationBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentTranslationBinding.inflate(inflater)
		return binding.root
	}

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

	override fun translationIntent(): Observable<String> =
		binding.translateButton.clicks()
			.map { binding.translatingInput.text.toString() }
			.let {
				val initialTranslation = navigator.getTranslationArguments(arguments)
				if (initialTranslation != null) {
					it.startWith(initialTranslation.first.content)
				} else {
					it
				}
			}

	override fun cancellationIntent(): Observable<Unit> =
		binding.cancelButton.clicks()

	override fun languageSelectionIntent(): Observable<Language> =
		binding.languagesLayout.languageSelection()

	override fun render(viewState: TranslationViewState) {
		renderCommonState(viewState)
		when (viewState) {
			is IdleTranslationViewState -> renderIdleState(viewState)
			is LoadingTranslationViewState -> renderLoadingState()
			is ErrorTranslationViewState -> renderErrorState(viewState)
		}
	}

	private fun renderCommonState(viewState: TranslationViewState) {
		binding.translatingInput.setText(viewState.textFrom.content)
		binding.languagesLayout.selectLanguage(viewState.textTo.language)
	}

	private fun renderIdleState(viewState: IdleTranslationViewState) {
		binding.translatingProgress.visibility = GONE
		binding.translateButton.isEnabled = true
		binding.cancelButton.visibility = GONE
		binding.translatingInput.isEnabled = true
		binding.translatedText.text = viewState.textTo.content
		binding.translatingInputLayout.error = null
		binding.languagesLayout.isEnabled = true
		if (viewState.counter != null) {
			binding.counterText.text = resources.getQuantityString(
				R.plurals.counter_text_format,
				viewState.counter,
				viewState.counter
			)
			binding.counterText.visibility = VISIBLE
		} else {
			binding.counterText.visibility = GONE
		}
		binding.translationResultCard.visibility = if (viewState.textTo.content.isEmpty()) GONE else VISIBLE
	}

	private fun renderLoadingState() {
		binding.translatingProgress.visibility = VISIBLE
		binding.translateButton.isEnabled = false
		binding.cancelButton.visibility = VISIBLE
		binding.translatingInput.isEnabled = false
		binding.translatedText.text = ""
		binding.translatingInputLayout.error = null
		binding.languagesLayout.isEnabled = false
		binding.counterText.text = ""
		binding.counterText.visibility = GONE
		binding.translationResultCard.visibility = GONE
	}

	private fun renderErrorState(viewState: ErrorTranslationViewState) {
		binding.translatingProgress.visibility = GONE
		binding.translateButton.isEnabled = true
		binding.cancelButton.visibility = GONE
		binding.translatingInput.isEnabled = true
		binding.translatedText.text = ""
		binding.translatingInputLayout.error = when (viewState.error) {
			ErrorTranslationViewState.ErrorType.CONNECTION ->
				getString(R.string.translation_error_connection)
			ErrorTranslationViewState.ErrorType.EMPTY_TEXT ->
				getString(R.string.translation_error_empty_text)
		}
		binding.languagesLayout.isEnabled = true
		binding.counterText.text = ""
		binding.counterText.visibility = GONE
		binding.translationResultCard.visibility = GONE
	}
}
