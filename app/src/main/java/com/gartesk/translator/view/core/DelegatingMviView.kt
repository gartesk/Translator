package com.gartesk.translator.view.core

import android.util.Log
import com.gartesk.mosbyx.mvi.MviPresenter
import com.gartesk.mosbyx.mvi.MviView
import com.gartesk.mosbyx.mvi.delegate.MviDelegateCallback
import com.gartesk.mosbyx.mvi.delegate.fragment.FragmentMviDelegate
import com.gartesk.mosbyx.mvi.delegate.fragment.FragmentMviDelegateImpl

abstract class DelegatingMviView<V : MviView, P : MviPresenter<V, *>>(
	protected val delegatedFragment: DelegatedMviFragment<*, *>
) : MviView, MviDelegateCallback<V, P> {

	private var restoringViewState: Boolean = false
	val mviDelegate: FragmentMviDelegate<V, P> by lazy {
		FragmentMviDelegateImpl(this, delegatedFragment)
	}

	override fun setRestoringViewState(restoringViewState: Boolean) {
		this.restoringViewState = restoringViewState
	}

	override val mviView: V
		get() = try {
			this as V
		} catch (exception: ClassCastException) {
			val msg =
				"Couldn't cast the View to the corresponding View interface. " +
						"Most likely you forgot to add " +
						"\"Fragment implements YourMvpViewInterface\".\""
			Log.e(this.toString(), msg)
			throw RuntimeException(msg, exception)
		}
}