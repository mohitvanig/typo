package com.vanistudios.typo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanistudios.typo.R
import com.vanistudios.typo.pojo.CheckboxItem

class CheckboxListAdapter(private val items: MutableList<CheckboxItem>) :
    RecyclerView.Adapter<CheckboxListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkboxlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox_item)
        private val textItem: TextView = itemView.findViewById(R.id.text_item)

        fun bind(item: CheckboxItem) {
            checkbox.isChecked = item.isChecked
            textItem.text = item.content

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }
        }
    }
}
