package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripRecord

class TripRecordListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_record_list)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val db = ZavbusDb.getInstance(applicationContext)
        val trip = getIntent().getSerializableExtra("trip") as Trip
        val listView = findViewById<ListView>(R.id.listView)

        setTitle("Участники " + trip.tripDates + " " + trip.name)

        val records = db?.tripRecordDao()?.getRecordsByTrip(trip.id) as Array<TripRecord>

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, records)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val tripRecord = parent.getAdapter().getItem(position) as TripRecord

            val intent = Intent(this, TripRecordActivity::class.java)
            intent.putExtra("tripRecord", tripRecord)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }

        initFilter(adapter)

    }

    private fun initFilter(adapter: ArrayAdapter<TripRecord>) {
        val filter = findViewById<EditText>(R.id.searchFilter)

        filter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }
        })
    }
}
