package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import org.json.JSONArray
import org.json.JSONObject
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripPacket
import ru.zavbus.zavbusexample.entities.TripRecord
import java.net.HttpURLConnection
import java.net.URL


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
                AsyncTaskHandler().execute("http://192.168.1.52:8090/api/curatorData")
            } catch (e: Exception) {

            }
        }
    }

    private fun getAdapter(trips: List<Trip>?): ArrayAdapter<Trip> {
        return ArrayAdapter(this, android.R.layout.simple_list_item_1, trips)
    }


/*

    private fun initData() {
        val db = ZavbusDb.getInstance(applicationContext)

        val trips = generateTrips()

        trips.forEach { trip ->
            Thread {
                db?.tripDao()?.insert(trip)
                generateRecords(trip).forEach { record ->
                    db?.tripRecordDao()?.insert(record)
                }
            }.start()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun generateTrips(): List<Trip> {

        val date = SimpleDateFormat("yyyy-MM-dd").parse("2018-03-07")
        val date2 = SimpleDateFormat("yyyy-MM-dd").parse("2018-03-08")
        val date3 = SimpleDateFormat("yyyy-MM-dd").parse("2018-03-09")

        val list = ArrayList<Trip>()
        list.add(Trip(1, "Завьялиха", date))
        list.add(Trip(2, "Евразия", date2))
        list.add(Trip(3, "Завьялиха", date3))
        return list
    }

    private fun generateRecords(trip: Trip): List<TripRecord> {
        val list = ArrayList<TripRecord>()
        list.add(TripRecord(trip.id + 1, trip.id, "Маслов Владимир", "+79123111025"))
        list.add(TripRecord(trip.id + 2, trip.id, "Самигуллин Евгений", "+79123111025"))
        list.add(TripRecord(trip.id + 3, trip.id, "Скорева Алена", "+79123111025"))
        return list
    }

    private fun initDataFromServer() {

    }
*/

    inner class AsyncTaskHandler : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            val text: String
            val connection = URL(url[0]).openConnection() as HttpURLConnection

            try {
                connection.connect()
                text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } finally {
                connection.disconnect()
            }
            return text
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleJson(result)
        }
    }

    private fun handleJson(jsonString: String?) {
        val jsonArray = JSONObject(jsonString).getJSONArray("trips");
        val db = ZavbusDb.getInstance(applicationContext)

        var x = 0
        while (x < jsonArray.length()) {
            val obj = jsonArray.getJSONObject(x)
            Thread {
                val trip = Trip(
                        id = obj.getLong("id"),
                        name = obj.getString("name"),
                        tripDates = obj.getString("tripDates"))
                db?.tripDao()?.insert(trip)

                insertRiderRecords(trip, obj.getJSONArray("records"))
                insertTripPackets(trip, obj.getJSONArray("packets"))

            }.start()
            x++
        }

        findViewById<ListView>(R.id.listView).adapter = getAdapter(db?.tripDao()?.getAll())
    }


    private fun insertRiderRecords(trip: Trip, records: JSONArray) {
        val db = ZavbusDb.getInstance(applicationContext)

        var x = 0
        while (x < records.length()) {
            val obj = records.getJSONObject(x)

            val tripRecord = TripRecord(
                    id = obj.getLong("id"),
                    tripId = trip.id,
                    name = obj.getString("lastName") + " " + obj.getString("firstName"),
                    phone = obj.getString("phone")
            )
            db?.tripRecordDao()?.insert(tripRecord)
            x++
        }
    }

    private fun insertTripPackets(trip: Trip, packets: JSONArray) {
        val db = ZavbusDb.getInstance(applicationContext)

        var x = 0
        while (x < packets.length()) {
            val obj = packets.getJSONObject(x)

            val packet = TripPacket(
                    id = obj.getLong("id"),
                    tripId = trip.id,
                    name = obj.getString("name"),
                    staff = obj.getString("stuff") == "true"
            )
            db?.tripPacketDao()?.insert(packet)
            x++
        }
    }
}
