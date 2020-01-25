package com.gartesk.translator.view.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gartesk.mosbyx.mvi.MviFragment
import com.gartesk.mosbyx.mvi.MviPresenter
import com.gartesk.mosbyx.mvi.MviView

abstract class DelegatedMviFragment<V : MviView, P : MviPresenter<V, *>> : MviFragment<V, P>() {

    private val delegatingViews: MutableList<DelegatingMviView<*, *>> = mutableListOf()

    protected fun registerDelegatingView(delegatingView: DelegatingMviView<*, *>) {
        delegatingViews.add(delegatingView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegatingViews.forEach { it.mviDelegate.onCreate(savedInstanceState) }
    }

    override fun onDestroy() {
        super.onDestroy()
        delegatingViews.forEach { it.mviDelegate.onDestroy() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegatingViews.forEach { it.mviDelegate.onSaveInstanceState(outState) }
    }

    override fun onPause() {
        super.onPause()
        delegatingViews.forEach { it.mviDelegate.onPause() }
    }

    override fun onResume() {
        super.onResume()
        delegatingViews.forEach { it.mviDelegate.onResume() }
    }

    override fun onStart() {
        super.onStart()
        delegatingViews.forEach { it.mviDelegate.onStart() }
    }

    override fun onStop() {
        super.onStop()
        delegatingViews.forEach { it.mviDelegate.onStop() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        delegatingViews.forEach { it.mviDelegate.onViewCreated(view, savedInstanceState) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        delegatingViews.forEach { it.mviDelegate.onDestroyView() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        delegatingViews.forEach { it.mviDelegate.onActivityCreated(savedInstanceState) }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        delegatingViews.forEach { it.mviDelegate.onAttach(activity) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        delegatingViews.forEach { it.mviDelegate.onAttach(context) }
    }

    override fun onDetach() {
        super.onDetach()
        delegatingViews.forEach { it.mviDelegate.onDetach() }
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        delegatingViews.forEach { it.mviDelegate.onAttachFragment(childFragment) }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        delegatingViews.forEach { it.mviDelegate.onConfigurationChanged(newConfig) }
    }
}