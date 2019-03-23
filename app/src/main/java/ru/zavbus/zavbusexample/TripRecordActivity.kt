package ru.zavbus.zavbusexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripRecord

class TripRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_record)

        val db = ZavbusDb.getInstance(applicationContext)

        val tripRecord = getIntent().getSerializableExtra("tripRecord") as TripRecord
        val trip = getIntent().getSerializableExtra("trip") as Trip
        val packets = db?.tripPacketDao()?.getPacketsByTrip(trip.id)

        setTitle(tripRecord.name);

        val packetsGroup: RadioGroup = findViewById<RadioGroup>(R.id.packets)

        var x = 0
        for (packet in packets!!) {
            val rb = RadioButton(this)
            rb.setText(packet.name)
            rb.setId(java.lang.Math.toIntExact(packet.id))
            packetsGroup.addView(rb)
        }
    }

}
