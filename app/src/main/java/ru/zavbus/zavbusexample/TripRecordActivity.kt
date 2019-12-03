package ru.zavbus.zavbusexample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import ru.zavbus.zavbusexample.adapter.TripServiceAdapter
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripPacket
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.entities.TripService

class TripRecordActivity : AppCompatActivity() {

    private var currentPacket: TripPacket? = null
    private var tripRecord: TripRecord? = null
    private var trip: Trip? = null
    private val db = ZavbusDb.getInstance(this)

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_record)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        tripRecord = intent.getSerializableExtra("tripRecord") as TripRecord
        trip = intent.getSerializableExtra("trip") as Trip
        val packets = db?.tripPacketDao()?.getPacketsByTrip(trip!!.id)
        val selectedPacket = db?.tripPacketDao()?.get(tripRecord!!.packetId)

        title = tripRecord!!.name

        val commentFromVk = findViewById<TextView>(R.id.commentFromVk)
        if (tripRecord?.commentFromVk!!.isEmpty()) {
            commentFromVk.setVisibility(View.GONE)
        } else {
            commentFromVk.text = tripRecord?.commentFromVk
        }

        val prepaidSumBlock = findViewById<LinearLayout>(R.id.prepaidSumBlock)
        if (tripRecord?.prepaidSum!! > 0) {
            prepaidSumBlock.findViewById<TextView>(R.id.prepaidSum).text = "" + tripRecord?.prepaidSum + " \u20BD"
        } else {
            prepaidSumBlock.setVisibility(View.GONE)
        }

//        findViewById<TextView>(R.id.prepaidSumText).text = "" + tripRecord?.prepaidSum + " \u20BD"

        val spinner: Spinner = findViewById(R.id.packets)

        ArrayAdapter<TripPacket>(this, R.layout.spinner_layout, packets).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        if (packets != null) {
            spinner.setSelection(packets.indexOf(selectedPacket))
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                val packet = packets?.get(position)
                currentPacket = packet
                packet?.let { initServices(it, tripRecord!!) }
                CountResultTask().execute(tripRecord)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }

        initConfirmTripRecordListener()
    }

    fun initServices(packet: TripPacket, tripRecord: TripRecord) {
        val services = db?.tripServiceDao()?.getServicesByPacket(packet.id)?.toCollection(ArrayList()) as ArrayList<TripService>
        val adapter = TripServiceAdapter(this, services, db, tripRecord)
        findViewById<ListView>(R.id.services).adapter = adapter
    }

    inner class CountResultTask : AsyncTask<TripRecord, String, TripRecord>() {
        //        val btn: Button = findViewById(R.id.confirmTripRecordButton)
        val resultSumText: TextView = findViewById(R.id.resultSumText)

        override fun doInBackground(vararg params: TripRecord): TripRecord {
            return params[0]
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(tripReocrd: TripRecord) {
            super.onPostExecute(tripReocrd)
            resultSumText.text = "" + Math.max(getPrice() - tripRecord?.prepaidSum!!, 0) + " \u20BD"
        }
    }

    inner class ConfirmTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            tripRecord!!.packetId = currentPacket!!.id
            val servicesInPacket = db?.tripServiceDao()?.getServicesByPacket(currentPacket!!.id)?.map { it.id } as ArrayList<Long>
            db.orderedTripServiceDao().deleteAllNotInServiceList(tripRecord!!.id, servicesInPacket)
            tripRecord!!.confirmed = true
            tripRecord!!.paidSumInBus = Math.max(getPrice() - tripRecord?.prepaidSum!!, 0)
            db.tripRecordDao().update(tripRecord!!)

            return null
        }
    }

    fun initConfirmTripRecordListener() {
        val btn: Button = findViewById(R.id.confirmTripRecordButton)

        btn.setOnClickListener {
            ConfirmTask().execute()
            val intent = Intent(this, TripRecordListActivity::class.java)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }
    }

    private fun getPrice(): Int {
        val price = db?.orderedTripServiceDao()
                ?.getAllOrderedServicesForRecordAndPacket(tripRecord!!.id, currentPacket!!.id)
                ?.sumBy { it.price }

        return price!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, TripRecordListActivity::class.java)
        myIntent.putExtra("trip", trip)
        startActivityForResult(myIntent, 0)
        return true
    }

    fun onChangeDiscount(item: TextView) {


    }


}
