package ru.zavbus.zavbusexample.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.text.HtmlCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Switch
import ru.zavbus.zavbusexample.R
import ru.zavbus.zavbusexample.TripRecordActivity
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.OrderedService
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.entities.TripService

class TripServiceAdapter(
        var context: Context,
        var layoutInflater: LayoutInflater,
        var list: ArrayList<TripService>,
        var db: ZavbusDb,
        var tripRecord: TripRecord
) : BaseAdapter() {

    constructor(context: Context, list: ArrayList<TripService>, db: ZavbusDb, tripRecord: TripRecord) : this(
            context, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, list, db, tripRecord
    )

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.service_item_row, parent, false)
        }

        val switcher = view?.findViewById<Switch>(R.id.serviceSwitcher)

        if (switcher != null) {
            val service = list.get(position)
            val hasOrderedService = db.orderedTripServiceDao().getAll(tripRecord.id, service.id).size > 0
            switcher.text = HtmlCompat.fromHtml("" + service.name + " <span style=\"color:#6F6F6F\">" + service.price + " \u20BD </span>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            switcher.isChecked = hasOrderedService

            setColorForOrderedService(switcher, hasOrderedService)

            if (service.mustHave) {
                switcher.isClickable = false
                switcher.isChecked = true
                initOrderedService(service, true)
                (switcher.parent as View).setBackgroundColor(Color.parseColor("#cecece"))
            }

            initServiceSwitcherListener(service, switcher, tripRecord)
        }

        return view
    }

    fun setColorForOrderedService(switcher: Switch, isChecked: Boolean) {
        (switcher.parent as View).setBackgroundResource(if (isChecked) R.color.customLightGreen else R.color.customWhite)
    }

    override fun getItem(position: Int): Any {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    private fun initServiceSwitcherListener(service: TripService, switcher: Switch, tripRecord: TripRecord) {
        switcher.setOnCheckedChangeListener { buttonView, isChecked ->
            initOrderedService(service, isChecked)
            setColorForOrderedService(switcher, isChecked)
        }
    }

    private fun initOrderedService(service: TripService, isChecked: Boolean) {
        Thread {
            db.orderedTripServiceDao().deleteAll(tripRecord.id, service.id)

            if (isChecked) {
                db.orderedTripServiceDao().insert(OrderedService(0, tripRecord.id, service.id))
            }
            (context as TripRecordActivity).CountResultTask().execute(tripRecord)
        }.start()
    }

}