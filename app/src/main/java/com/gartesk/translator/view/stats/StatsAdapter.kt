package com.gartesk.translator.view.stats

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.gartesk.translator.R
import com.gartesk.translator.databinding.ItemCounterBinding
import com.gartesk.translator.databinding.ItemStatBinding
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Stat
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.totalCounter

class StatsAdapter(
	private val onCounterClick: (Text, Language) -> Unit
) : RecyclerView.Adapter<StatViewHolder>() {

	var items: List<Stat> = emptyList()
		set(value) {
			val oldSize = itemCount
			field = value
			val newSize = itemCount
			previousExpandedPosition = null
			currentExpandedPosition = null
			if (oldSize > newSize) {
				notifyItemRangeChanged(0, newSize)
				notifyItemRangeRemoved(newSize, oldSize - newSize)
			} else {
				notifyItemRangeChanged(0, oldSize)
				notifyItemRangeInserted(oldSize, newSize - oldSize)
			}
		}

	private var previousExpandedPosition: Int? = null
	private var currentExpandedPosition: Int? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
		val itemViewBinding = ItemStatBinding.inflate(LayoutInflater.from(parent.context))
		return StatViewHolder(onCounterClick, itemViewBinding)
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

class StatViewHolder(
	private val onCounterClick: (Text, Language) -> Unit,
	private val itemViewBinding: ItemStatBinding
) : RecyclerView.ViewHolder(itemViewBinding.root) {

	fun bind(stat: Stat, expanded: Boolean, changedExpansionState: Boolean, expandTrigger: () -> Unit) {
		itemViewBinding.textFrom.text = stat.from.content
		itemViewBinding.languageFrom.text = stat.from.language.code
		itemViewBinding.counterText.text = stat.totalCounter.toString()
		itemViewBinding.countersContainer.removeAllViews()
		stat.counters.forEach { statCounter ->
			bindCounterView(stat.from, statCounter, itemViewBinding.countersContainer)
		}

		if (expanded) {
			itemViewBinding.countersContainer.visibility = VISIBLE
			if (changedExpansionState) {
				itemViewBinding.expandIcon.setImageResource(R.drawable.avd_expand_to_collapse)
			} else {
				itemViewBinding.expandIcon.setImageResource(R.drawable.ic_collapse)
			}
		} else {
			itemViewBinding.countersContainer.visibility = GONE
			if (changedExpansionState) {
				itemViewBinding.expandIcon.setImageResource(R.drawable.avd_collapse_to_expand)
			} else {
				itemViewBinding.expandIcon.setImageResource(R.drawable.ic_expand)
			}
		}
		if (changedExpansionState) {
			(itemViewBinding.expandIcon.drawable as? Animatable)?.start()
		}

		itemView.setOnClickListener { expandTrigger() }
	}

	private fun bindCounterView(text: Text, statCounter: Stat.Counter, container: ViewGroup) {
		val counterViewBinding = ItemCounterBinding.inflate(LayoutInflater.from(container.context))
		counterViewBinding.root.setOnClickListener { onCounterClick(text, statCounter.language) }
		counterViewBinding.languageTo.text = statCounter.language.code
		counterViewBinding.counterText.text = statCounter.value.toString()
		container.addView(counterViewBinding.root)
	}
}