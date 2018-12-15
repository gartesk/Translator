package com.gartesk.translator.view.core

import android.util.Log
import com.hannesdorfmann.mosby3.FragmentMviDelegate
import com.hannesdorfmann.mosby3.FragmentMviDelegateImpl
import com.hannesdorfmann.mosby3.MviDelegateCallback
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import kotlin.ClassCastException

abstract class DelegatingMviView<V : MvpView, P : MviPresenter<V, *>>(
    protected val delegatedFragment: DelegatedMviFragment<*, *>
) : MvpView, MviDelegateCallback<V, P> {

    private var restoringViewState: Boolean = false
    val mviDelegate: FragmentMviDelegate<V, P> by lazy {
        FragmentMviDelegateImpl<V, P>(this, delegatedFragment)
    }

    override fun setRestoringViewState(restoringViewState: Boolean) {
        this.restoringViewState = restoringViewState
    }

    override fun getMvpView(): V {
        try {
            return this as V
        } catch (exception: ClassCastException) {
            val msg =
                "Couldn't cast the View to the corresponding View interface. " +
                        "Most likely you forgot to add " +
                        "\"Fragment implements YourMvpViewInterface\".\""
            Log.e(this.toString(), msg)
            throw RuntimeException(msg, exception)
        }
    }
}