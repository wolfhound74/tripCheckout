package ru.zavbus.zavbusexample.services

import android.content.Context
import android.os.AsyncTask
import android.widget.ArrayAdapter
import android.widget.ListView
import org.json.JSONArray
import org.json.JSONObject
import ru.zavbus.zavbusexample.R
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.*
import java.net.HttpURLConnection
import java.net.URL

class InitDataService(val applicationContext: Context, val listView: ListView) {

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

                insertTripPackets(trip, obj.getJSONArray("packets"))
                insertRiderRecords(trip, obj.getJSONArray("records"))

            }.start()
            x++
        }

       listView.adapter = getAdapter(db?.tripDao()?.getAll())
    }

    private fun getAdapter(trips: List<Trip>?): ArrayAdapter<Trip> {
        return  ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, trips)
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
            insertOrderedServices(tripRecord, obj.getJSONArray("orderedServices"))
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

            insertPacketServices(packet, obj.getJSONArray("services"))
            x++
        }
    }

    private fun insertPacketServices(packet: TripPacket, services: JSONArray) {
        val db = ZavbusDb.getInstance(applicationContext)

        var x = 0
        while (x < services.length()) {
            val obj = services.getJSONObject(x)

            val service = TripService(
                    id = obj.getLong("id"),
                    tripPacketId = packet.id,
                    name = obj.getString("name"),
                    price = obj.getLong("price"),
                    rent = obj.getString("mustHave") == "true" //todo поправить этот параметр
            )
            db?.tripServiceDao()?.insert(service)
            x++
        }
    }

    private fun insertOrderedServices(tripRecord: TripRecord, orderedServices: JSONArray) {
        val db = ZavbusDb.getInstance(applicationContext)

        var x = 0
        while (x < orderedServices.length()) {
            val obj = orderedServices.getJSONObject(x)

            val os = OrderedService(0,
                    tripRecordId = tripRecord.id,
                    tripServiceId = obj.getLong("serviceId")
            )
            db?.orderedTripServiceDao()?.insert(os)

            x++
        }
    }
}