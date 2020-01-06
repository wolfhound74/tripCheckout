package ru.zavbus.zavbusexample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import ru.zavbus.zavbusexample.adapter.TripRecordListAdapter
import ru.zavbus.zavbusexample.commandObjects.PlusOneTripRecordsCommand
import ru.zavbus.zavbusexample.commandObjects.TripRecordListCommand
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.services.PriceService

class PlusOneTripRecordsActivity : AppCompatActivity() {

    private var trip: Trip? = null
    private var plusOneTripRecordIds: ArrayList<Long>? = null
    private val db = ZavbusDb.getInstance(this)

    val priceService: PriceService = PriceService(this)

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureActivity()

        initRecordsList(plusOneTripRecordIds!!)

        initConfirmTripRecordListener()
        initPlusOneTripRecordListener()
    }

    fun initRecordsList(plusOneTripRecordIds: ArrayList<Long>) {
        val listView = findViewById<ListView>(R.id.plusOneTripRecords)
        val records = db?.tripRecordDao()?.getRecordsByIds(plusOneTripRecordIds)

        val adapter = TripRecordListAdapter(this, records!!, plusOneTripRecordIds)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val tripRecord = parent.getAdapter().getItem(position) as TripRecord

            val intent = Intent(this, TripRecordActivity::class.java)
            intent.putExtra("tripRecord", tripRecord)
            intent.putExtra("trip", trip)
            intent.putExtra("plusOneTripRecordIds", plusOneTripRecordIds)
            startActivity(intent)
        }
    }

    fun initConfirmTripRecordListener(): Void? {
        val btn: Button = findViewById(R.id.confirmAllTripRecordsButton)

        btn.setOnClickListener {
            ConfirmPlusOneTripRecordsTask().execute()

            val intent = Intent(this, TripRecordListActivity::class.java)
            intent.putExtra("cmd", TripRecordListCommand(trip!!))
            startActivity(intent)
        }
        return null
    }

    inner class ConfirmPlusOneTripRecordsTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            db?.tripRecordDao()?.confirmRecords(plusOneTripRecordIds!!)
            return null
        }
    }


    fun initPlusOneTripRecordListener(): Void? {
        val btn: Button = findViewById(R.id.plusOne)

        btn.setOnClickListener {
            val intent = Intent(this, TripRecordListActivity::class.java)
            intent.putExtra("cmd", TripRecordListCommand(trip!!, plusOneTripRecordIds!!))
            startActivity(intent)
        }
        return null
    }

    inner class PlusOneTripRecordsCountResultTask : AsyncTask<ArrayList<Long>, String, ArrayList<Long>>() {
        val resultSumText: TextView = findViewById(R.id.resultSumText)

        override fun doInBackground(vararg params: ArrayList<Long>): ArrayList<Long> {
            return params[0]
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(plusOneTripRecordIds: ArrayList<Long>) {
            super.onPostExecute(plusOneTripRecordIds)
            val sum = db?.tripRecordDao()?.getFullSumForRecords(plusOneTripRecordIds)

            resultSumText.text = "$sum \u20BD"
        }
    }

    private fun configureActivity() {
        setContentView(R.layout.activity_plus_one_trip_records)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        val cmd = intent.getSerializableExtra("cmd") as PlusOneTripRecordsCommand

        trip = cmd.trip
        plusOneTripRecordIds = cmd.plusOneTripRecordIds

        title = "+ 1"

        PlusOneTripRecordsCountResultTask().execute(plusOneTripRecordIds)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, TripRecordListActivity::class.java)
        myIntent.putExtra("cmd", TripRecordListCommand(trip!!, plusOneTripRecordIds!!))
        startActivityForResult(myIntent, 0)
        return true
    }

}
