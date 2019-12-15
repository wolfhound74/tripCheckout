package ru.zavbus.zavbusexample.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import ru.zavbus.zavbusexample.R
import ru.zavbus.zavbusexample.entities.TripRecord


class TripRecordTogetherListAdapter(
        var context: Context,
        var layoutInflater: LayoutInflater,
        val records: ArrayList<TripRecord>) : BaseAdapter() {

    private var filteredData: ArrayList<TripRecord> = records

    constructor(context: Context, list: ArrayList<TripRecord>) : this(
            context, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, list
    )

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_record_row, parent, false)
        }

        val tripRecord = filteredData.get(position)
        val riderNameView = view?.findViewById<TextView>(R.id.riderName)
        val paidSumView = view?.findViewById<TextView>(R.id.paidSum)

        val text = tripRecord.toString()
        var color = R.color.customLightBlue

        riderNameView?.text = " + " + text
        paidSumView?.text = "" + tripRecord.paidSumInBus + " \u20BD"
        (riderNameView?.parent as View).setBackgroundResource(color)

        return view!!
    }

    override fun getItem(position: Int): Any {
        return filteredData.get(position)
    }

    override fun getItemId(position: Int): Long {
        return filteredData.get(position).id
    }

    override fun getCount(): Int {
        return filteredData.size
    }
}