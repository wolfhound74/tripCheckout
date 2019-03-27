package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.services.InitDataService


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("Выезды");

        val db = ZavbusDb.getInstance(applicationContext)

        val listView = findViewById<ListView>(R.id.listView)

        val adapter = getAdapter(db?.tripDao()?.getAll())
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val trip = parent.getAdapter().getItem(position) as Trip

            val intent = Intent(this, TripRecordListActivity::class.java)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            try {
                InitDataService(this, listView)
                        .AsyncTaskHandler()
                        .execute("http://192.168.1.52:8090/api/curatorData")
            } catch (e: Exception) {

            }
        }
    }

    private fun getAdapter(trips: List<Trip>?): ArrayAdapter<Trip> {
        return ArrayAdapter(this, android.R.layout.simple_list_item_1, trips)
    }
}
