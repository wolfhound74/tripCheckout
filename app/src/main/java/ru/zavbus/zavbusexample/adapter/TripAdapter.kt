package ru.zavbus.zavbusexample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.zavbus.zavbusexample.R
import ru.zavbus.zavbusexample.entities.Trip


class TripAdapter(
        var context: Context,
        var layoutInflater: LayoutInflater,
        val trips: List<Trip>) : BaseAdapter() {

    constructor(context: Context, list: List<Trip>) : this(
            context, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, list
    )


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_row, parent, false)
        }

        val trip = trips.get(position)

        view?.findViewById<TextView>(R.id.tripText)?.setText(trip.toString())

        return view!!
    }

    override fun getItem(position: Int): Any {
        return trips.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return trips.size
    }


}