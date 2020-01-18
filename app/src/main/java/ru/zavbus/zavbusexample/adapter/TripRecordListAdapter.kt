package ru.zavbus.zavbusexample.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.zavbus.zavbusexample.R
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.TripRecord


class TripRecordListAdapter(
        var context: Context,
        var layoutInflater: LayoutInflater,
        val records: Array<TripRecord>,
        val plusOneRecordIds: ArrayList<Long>
) : BaseAdapter(), Filterable {

    private var filteredData: Array<TripRecord> = records
    private var mFilter = ItemFilter()

    private val db = ZavbusDb.getInstance(context)

    constructor(context: Context, list: Array<TripRecord>, list2: ArrayList<Long>) : this(
            context, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, list, list2
    )


    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_record_row, parent, false)
        }


        val tripRecord = filteredData.get(position)
        val riderNameView = view?.findViewById<TextView>(R.id.riderName)
        val packetName = view?.findViewById<TextView>(R.id.packetName)
        val paidSumView = view?.findViewById<TextView>(R.id.paidSum)

        val text = tripRecord.toString()
        var color = Color.WHITE
        var paidSum = ""

        if (plusOneRecordIds.contains(tripRecord.id)) {
            color = ContextCompat.getColor(context, R.color.customLightBlue)
            paidSum = "" + tripRecord.paidSumInBus + " \u20BD"
        } else if (tripRecord.confirmed!!) {
            color = ContextCompat.getColor(context, R.color.customLightGreen)
            paidSum = "" + tripRecord.paidSumInBus + " \u20BD"
        }

        if (tripRecord.moneyBack!! > 0) {
            paidSum = "\uD83D\uDCB6 " + paidSum
        }

        //todo пофиксить, чтобы каждый раз не итерироваться
        val packet = db?.tripPacketDao()?.get(tripRecord.packetId)!!

        riderNameView?.text = text
        packetName?.text = packet.name
        paidSumView?.text = paidSum

        val services = db.orderedTripServiceDao()
                .getAllNotMustHaveOrderedServicesForRecordAndPacket(tripRecord.id, packet.id)
                .joinToString(separator = ", ") { it.name }

        if (services.isNotEmpty()) {
            packetName?.text = "${packetName?.text} + $services"
        }

        view?.findViewById<LinearLayout>(R.id.tripRecord)?.setBackgroundColor(color)

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