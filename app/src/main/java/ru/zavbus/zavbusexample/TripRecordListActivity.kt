package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.WindowManager
import android.widget.EditText
import android.widget.ListView
import ru.zavbus.zavbusexample.adapter.TripRecordListAdapter
import ru.zavbus.zavbusexample.adapter.TripRecordTogetherListAdapter
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripRecord

class TripRecordListActivity : AppCompatActivity() {

    private var ridersTogether: ArrayList<TripRecord>? = arrayListOf<TripRecord>()
    private var trip: Trip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_trip_record_list)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val db = ZavbusDb.getInstance(applicationContext)
        trip = getIntent().getSerializableExtra("trip") as Trip
        val ridersTogetherFromIntent = getIntent().getSerializableExtra("ridersTogether") as ArrayList<TripRecord>?

        ridersTogether = ridersTogetherFromIntent ?: ridersTogether

        val records = db?.tripRecordDao()?.getRecordsByTrip(trip!!.id) as Array<TripRecord>
        setTitle(trip!!.tripDates + " " + trip!!.name)


        val tpTogetherView = findViewById<ListView>(R.id.listOfRidersTogether)
        tpTogetherView.adapter = ridersTogether?.let { TripRecordTogetherListAdapter(this, it) }

        val listView = findViewById<ListView>(R.id.listView)
        val adapter = TripRecordListAdapter(this, records)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val tripRecord = parent.getAdapter().getItem(position) as TripRecord

            val intent = Intent(this, TripRecordActivity::class.java)
            intent.putExtra("tripRecord", tripRecord)
            intent.putExtra("trip", trip)
            intent.putExtra("ridersTogether", ridersTogether)
            startActivity(intent)
        }

        initFilter(adapter)
    }

    private fun initFilter(adapter: TripRecordListAdapter) {
        val filter = findViewById<EditText>(R.id.searchFilter)

        filter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, TripActivity::class.java)
        myIntent.putExtra("trip", trip)
        startActivityForResult(myIntent, 0)
        return true
    }
}
