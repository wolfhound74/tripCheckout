package ru.zavbus.zavbusexample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import ru.zavbus.zavbusexample.adapter.TripRecordListAdapter
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripRecord

class TripRecordListActivity : AppCompatActivity() {

    private var plusOneTripRecordIds: ArrayList<Long>? = arrayListOf<Long>()
    private var trip: Trip? = null
    private val db = ZavbusDb.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_trip_record_list)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        trip = getIntent().getSerializableExtra("trip") as Trip
        plusOneTripRecordIds = getIntent().getSerializableExtra("plusOneTripRecordIds") as ArrayList<Long>?
                ?: plusOneTripRecordIds

        setTitle(trip!!.tripDates + " " + trip!!.name)

        initPlusOnetripReordsInfo(plusOneTripRecordIds!!)

        val records = db?.tripRecordDao()?.getRecordsByTrip(trip!!.id) as Array<TripRecord>
        val listView = findViewById<ListView>(R.id.listView)
        val adapter = TripRecordListAdapter(this, records, plusOneTripRecordIds!!)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val tripRecord = parent.getAdapter().getItem(position) as TripRecord

            val intent = Intent(this, TripRecordActivity::class.java)
            intent.putExtra("tripRecord", tripRecord)
            intent.putExtra("trip", trip)
            intent.putExtra("plusOneTripRecordIds", plusOneTripRecordIds)
            startActivity(intent)
        }

        initFilter(adapter)
    }

    @SuppressLint("SetTextI18n")
    private fun initPlusOnetripReordsInfo(plusOneTripRecordIds: ArrayList<Long>) {
        if (plusOneTripRecordIds.size > 0) {
//            val plusOneRecords = db?.tripRecordDao()?.getRecordsByIds(plusOneTripRecordIds)

            val plusOneInfo = findViewById<TextView>(R.id.plusOneInfo)
            plusOneInfo.text = "+ " + plusOneTripRecordIds.size + " чел"
            plusOneInfo.visibility = View.VISIBLE
        }
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
