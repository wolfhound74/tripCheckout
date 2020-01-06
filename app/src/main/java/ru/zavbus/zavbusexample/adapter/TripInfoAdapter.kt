package ru.zavbus.zavbusexample.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.zavbus.zavbusexample.R


class TripInfoAdapter(
        var context: Context,
        var layoutInflater: LayoutInflater,
        val actions: MutableList<Map<String, Any>>) : BaseAdapter() {

    constructor(context: Context, list: MutableList<Map<String, Any>>) : this(
            context, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, list
    )

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_info_row, parent, false)
        }

        val action: Map<String, Any> = actions.get(position)

        view?.findViewById<TextView>(R.id.tripInfoText)?.setText("" + action.get("action"))
        view?.findViewById<TextView>(R.id.tripInfoText2)?.setText("" + action.get("info"))

        return view!!
    }

    override fun getItem(position: Int): Any {
        return actions.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return actions.size
    }


}