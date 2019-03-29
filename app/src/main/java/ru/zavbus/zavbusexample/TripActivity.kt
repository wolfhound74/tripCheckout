package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import ru.zavbus.zavbusexample.entities.Trip


class TripActivity : AppCompatActivity() {

    val actions = arrayOf("Участники выезда", "Прокат", "Новички")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val trip = getIntent().getSerializableExtra("trip") as Trip
        setTitle(trip.toString())

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val listActions = findViewById<ListView>(R.id.tripActions)

        listActions.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, actions)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(myIntent, 0)
        return true
    }
}
