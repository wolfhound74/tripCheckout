package ru.zavbus.zavbusexample

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
import java.util.*


class TripRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_record)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val db = ZavbusDb.getInstance(applicationContext)

        val tripRecord = intent.getSerializableExtra("tripRecord") as TripRecord
        val trip = intent.getSerializableExtra("trip") as Trip
        val packets = db?.tripPacketDao()?.getPacketsByTrip(trip.id)
        val selectedPacket = db?.tripPacketDao()?.get(tripRecord.packetId)

        title = tripRecord.name

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
                packet?.let { initServices(it, tripRecord) }
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
        CountResultTask().execute(tripRecord)
    }

    fun initServices(packet: TripPacket, tripRecord: TripRecord) {
        val db = ZavbusDb.getInstance(applicationContext)
        val services = db?.tripServiceDao()?.getServicesByPacket(packet.id)?.toCollection(ArrayList()) as ArrayList<TripService>
        val adapter = TripServiceAdapter(this, services, db, tripRecord)
        findViewById<ListView>(R.id.services).adapter = adapter
    }

    inner class CountResultTask : AsyncTask<TripRecord, String, TripRecord>() {
        val btn: Button = findViewById(R.id.confirmTripRecordButton)

        override fun doInBackground(vararg params: TripRecord?): TripRecord {
            val record = params[0] as TripRecord
            return record
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(tripReocrd: TripRecord?) {
            super.onPostExecute(tripReocrd)

            val price = tripReocrd?.id?.let {
                ZavbusDb.getInstance(applicationContext)?.orderedTripServiceDao()?.getAllServices(it)
            }?.sumBy { it.price }
            btn.text = "Подтвердить " + price
        }
    }

}
