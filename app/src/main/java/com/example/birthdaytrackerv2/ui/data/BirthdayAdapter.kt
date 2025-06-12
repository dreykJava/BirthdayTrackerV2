package com.example.birthdaytrackerv2.ui.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.birthdaytrackerv2.R

class BirthdayAdapter(
    context: Context,
    private val items: List<BirthdayNote>,
    private val onDeleteClickListener: (Int) -> Unit
) : ArrayAdapter<BirthdayNote>(context, R.layout.item_list_view, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View  {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_list_view, parent, false)

        val item = items[position]

        val nameTextView: TextView = itemView.findViewById(R.id.birthday_man_name)
        val dateTextView: TextView = itemView.findViewById(R.id.birthday_man_date)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_item_list_btn)

        nameTextView.text = item.name
        dateTextView.text = concatDate(item.date_day, item.date_month, item.date_year)

        deleteBtn.setOnClickListener {
            onDeleteClickListener(position)
        }

        return itemView
    }

    private fun concatDate(date_day: String, date_month: String, date_year: String?) : String {
        val concatString = StringBuilder()
        concatString.append(date_day)
        concatString.append(".")
        concatString.append(date_month)

        if (date_year != null) {
            concatString.append(".")
            concatString.append(date_year)
        }

        return concatString.toString()
    }
}