package com.gartesk.translator.view.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gartesk.translator.R
import com.gartesk.translator.domain.entity.Translation
import kotlinx.android.synthetic.main.item_stat.view.*

class StatsAdapter : RecyclerView.Adapter<StatsViewHolder>() {

	var items: List<Translation> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
		val itemView = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_stat, parent, false)
		return StatsViewHolder(itemView)
	}

	override fun getItemCount(): Int = items.size

	override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
		holder.bind(items[position])
	}
}

class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

	fun bind(translation: Translation) {
		itemView.textFrom.text = translation.from.content
		itemView.languageFrom.text = translation.from.language.code
		itemView.textTo.text = translation.to.content
		itemView.languageTo.text = translation.to.language.code
		itemView.counterText.text = itemView.context.resources.getQuantityString(
			R.plurals.counter_text_format,
			translation.counter,
			translation.counter
		)
	}
}