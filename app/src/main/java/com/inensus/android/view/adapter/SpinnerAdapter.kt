package com.inensus.android.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class SpinnerAdapter<T>(context: Context, textViewResourceId: Int = android.R.layout.simple_spinner_item, private val values: List<T>) : ArrayAdapter<T>(context, textViewResourceId, values) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): T? {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView

        label.text = values[position].toString()
        label.textSize = 14f

        return label
    }

    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView

        label.text = values[position].toString()

        if (position == 0) {
            label.setTextColor(Color.GRAY)
        } else {
            label.setTextColor(Color.BLACK)
        }

        label.textSize = 14f

        return label
    }
}