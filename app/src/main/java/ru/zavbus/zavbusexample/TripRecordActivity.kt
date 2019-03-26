package ru.zavbus.zavbusexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import ru.zavbus.zavbusexample.adapter.TripServiceAdapter
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripPacket
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.entities.TripService


class TripRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_record)

        val db = ZavbusDb.getInstance(applicationContext)

        val tripRecord = getIntent().getSerializableExtra("tripRecord") as TripRecord
        val trip = getIntent().getSerializableExtra("trip") as Trip
        val packets = db?.tripPacketDao()?.getPacketsByTrip(trip.id)

        setTitle(tripRecord.name)

        val spinner: Spinner = findViewById(R.id.packets)

        ArrayAdapter<TripPacket>(this, R.layout.spinner_layout, packets).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                val packet = packets?.get(position)
                packet?.let { initServices(it, tripRecord) }
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    fun initServices(packet: TripPacket, tripRecord: TripRecord) {
        val db = ZavbusDb.getInstance(applicationContext)
        val services = db?.tripServiceDao()?.getServicesByPacket(packet.id)?.toCollection(ArrayList()) as ArrayList<TripService>
        val adapter = TripServiceAdapter(this, services, db, tripRecord)
        findViewById<ListView>(R.id.services).setAdapter(adapter)
    }

}
