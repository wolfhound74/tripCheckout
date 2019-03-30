package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ListView
import android.widget.TextView
import ru.zavbus.zavbusexample.adapter.TripInfoAdapter
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.services.SendingDataService


class TripActivity : AppCompatActivity() {

    private var trip: Trip? = null
    private val db = ZavbusDb.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureActivity()

        initTripInfo()

        findViewById<TextView>(R.id.tripNote).text = trip?.note
    }

    private fun initTripInfo() {
        val listActions = findViewById<ListView>(R.id.tripActions)
        val adapter = TripInfoAdapter(this, getActions())

        listActions.adapter = adapter
        listActions.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val intent = Intent(this, TripRecordListActivity::class.java)
                    intent.putExtra("trip", trip)
                    startActivity(intent)
                }
                else -> {

                }
            }
        }
    }

    private fun getActions(): MutableList<Map<String, String>> {
        val items: MutableList<Map<String, String>> = mutableListOf()
        items.add(HashMap(
                hashMapOf("action" to "Участники выезда", "info" to "⟩")
        ))

        val services = db?.tripServiceDao()?.getServicesForTrip(trip?.id!!)?.distinctBy { it.serviceId }
        services?.forEach { s ->
            val size = db?.orderedTripServiceDao()?.getAllOrderedServices(trip?.id!!, s.id)?.size

            items.add(HashMap(
                    hashMapOf("action" to s.name, "info" to "" + size)
            ))
        }

        val records = db?.tripRecordDao()?.getAllConfirmedRecords(trip!!.id)
        val paidSumInBus = db?.tripRecordDao()?.getAllPaidSumInBus(trip!!.id)

        items.add(HashMap(hashMapOf("action" to "Участники", "info" to "" + records?.size)))
        items.add(HashMap(hashMapOf("action" to "Денег собрано", "info" to "" + paidSumInBus + " \u20BD")))

        return items
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.trip_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.sendData -> {
            try {
                SendingDataService(this).AsyncTaskHandler().execute(trip)
            } catch (e: Exception) {

            }
            true
        }

        else -> {
            //todo пофиксить это условие
            val myIntent = Intent(applicationContext, MainActivity::class.java)
            startActivityForResult(myIntent, 0)
            super.onOptionsItemSelected(item)
        }


    }

    private fun configureActivity() {
        setContentView(R.layout.activity_trip)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        trip = getIntent().getSerializableExtra("trip") as Trip
        setTitle(trip.toString())
    }

}

