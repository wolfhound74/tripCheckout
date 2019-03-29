package ru.zavbus.zavbusexample.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import ru.zavbus.zavbusexample.R
import ru.zavbus.zavbusexample.entities.TripRecord


class TripRecordListAdapter(
        var context: Context,
        var layoutInflater: LayoutInflater,
        val records: Array<TripRecord>) : BaseAdapter(), Filterable {

    private var filteredData: Array<TripRecord> = records
    private var mFilter = ItemFilter()

    constructor(context: Context, list: Array<TripRecord>) : this(
            context, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, list
    )


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_record_row, parent, false)
        }


        val tripRecord = filteredData.get(position)
        val textView = view?.findViewById<TextView>(R.id.tripRecordText)

        var text = tripRecord.toString()
        var color = Color.WHITE
        if (tripRecord.confirmed!!) {
            text = "\uD83D\uDC4D " + text
            color = Color.parseColor("#008000")
        }
        textView?.setText(text)
        (textView?.parent as View).setBackgroundColor(color)

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

    override fun getFilter(): Filter {
        return mFilter
    }

    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

            val filterString = constraint.toString().toLowerCase()
            val results = Filter.FilterResults()
            val count = records.size
            val nlist = ArrayList<String>(count)

            var filterableString: String

            for (i in 0 until count) {
                filterableString = records.get(i).toString()
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString)
                }
            }

            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            val resultsValues = results.values as ArrayList<*>
            filteredData = records.filter { it.name in resultsValues }.toTypedArray()

            notifyDataSetChanged()
        }

    }
}