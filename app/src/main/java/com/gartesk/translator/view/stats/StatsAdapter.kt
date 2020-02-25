package com.gartesk.translator.view.stats

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Stat
import com.gartesk.translator.domain.entity.totalCounter
import kotlinx.android.synthetic.main.item_counter.view.*
import kotlinx.android.synthetic.main.item_stat.view.*
import kotlinx.android.synthetic.main.item_stat.view.counterText

class StatsAdapter : RecyclerView.Adapter<StatViewHolder>() {

	var items: List<Stat> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
		}

	private var previousExpandedPosition: Int? = null
	private var currentExpandedPosition: Int? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
		val itemView = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_stat, parent, false)
		return StatViewHolder(itemView)
	}

	override fun getItemCount(): Int = items.size

	override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
		val expanded = position == currentExpandedPosition
		val changedToCollapsed = currentExpandedPosition != position && previousExpandedPosition == position
		val changedToExpanded = currentExpandedPosition == position && previousExpandedPosition != position
		val changedExpansionState = changedToCollapsed || changedToExpanded
		holder.bind(items[position], expanded, changedExpansionState) {
			previousExpandedPosition = currentExpandedPosition

			currentExpandedPosition = if (previousExpandedPosition == position) null else position

			previousExpandedPosition?.let { notifyItemChanged(it) }
			currentExpandedPosition?.let { notifyItemChanged(it) }
		}
	}

	override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
		(recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
	}
}

class StatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

	fun bind(stat: Stat, expanded: Boolean, changedExpansionState: Boolean, expandTrigger: () -> Unit) {
		itemView.textFrom.text = stat.from.content
		itemView.languageFrom.text = stat.from.language.code
		itemView.counterText.text = stat.totalCounter.toString()
		itemView.countersContainer.removeAllViews()
		stat.counters.forEach { statCounter ->
			bindCounterView(statCounter, itemView.countersContainer)
		}

		if (expanded) {
			itemView.countersContainer.visibility = VISIBLE
			if (changedExpansionState) {
				itemView.expandIcon.setImageResource(R.drawable.avd_expand_to_collapse)
			} else {
				itemView.expandIcon.setImageResource(R.drawable.ic_collapse)
			}
		} else {
			itemView.countersContainer.visibility = GONE
			if (changedExpansionState) {
				itemView.expandIcon.setImageResource(R.drawable.avd_collapse_to_expand)
			} else {
				itemView.expandIcon.setImageResource(R.drawable.ic_expand)
			}
		}
		if (changedExpansionState) {
			(itemView.expandIcon.drawable as? Animatable)?.start()
		}

		itemView.setOnClickListener { expandTrigger() }
	}

	private fun bindCounterView(statCounter: Stat.Counter, container: ViewGroup) {
		val counterView = LayoutInflater.from(container.context)
			.inflate(R.layout.item_counter, container, false)
		counterView.languageTo.text = statCounter.language.code
		counterView.counterText.text = statCounter.value.toString()
		container.addView(counterView)
	}
}