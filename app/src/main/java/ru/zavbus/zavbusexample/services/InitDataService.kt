package ru.zavbus.zavbusexample.services

import android.content.Context
import android.os.AsyncTask
import android.widget.ArrayAdapter
import android.widget.ListView
import org.json.JSONArray
import org.json.JSONObject
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

            val trip = Trip(
                    id = obj.getLong("id"),
                    name = obj.getString("name"),
                    note = if (!obj.isNull("note")) obj.getString("note") else "",
                    tripDates = obj.getString("tripDates"))
            db?.tripDao()?.insert(trip)

            insertTripPackets(trip, obj.getJSONArray("packets"))
            insertRiderRecords(trip, obj.getJSONArray("records"))

            x++
        }

        listView.adapter = getAdapter(db?.tripDao()?.getAll())
    }

    private fun getAdapter(trips: List<Trip>?): ArrayAdapter<Trip> {
        return ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, trips)
    }


    private fun insertRiderRecords(trip: Trip, records: JSONArray) {
        val db = ZavbusDb.getInstance(applicationContext)

        var x = 0
        while (x < records.length()) {
            val obj = records.getJSONObject(x)
            val tripRecord = TripRecord(
                    id = obj.getLong("id"),
                    recordId = obj.getLong("recordId"),
                    tripId = trip.id,
                    mainRiderId = if (!obj.isNull("mainRiderId")) obj.getLong("mainRiderId") else null,
                    name = obj.getString("lastName") + " " + obj.getString("firstName"),
                    commentFromVk = if (!obj.isNull("commentFromVk")) obj.getString("commentFromVk") else "",
                    orderedKit = if (!obj.isNull("orderedKit")) obj.getString("orderedKit") else "",
                    prepaidSum = if (!obj.isNull("prepaidSum")) obj.getInt("prepaidSum") else 0,
                    discountSum = if (!obj.isNull("discountSum")) obj.getInt("discountSum") else 0,
                    moneyBack = if (!obj.isNull("moneyBack")) obj.getInt("moneyBack") else 0,
                    packetId = obj.getLong("packetId"),
                    phone = obj.getString("phone"),
                    paidSumInBus = 0,
                    confirmed = false
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
                    price = obj.getInt("price"),
                    serviceId = obj.getLong("serviceId"),
                    mustHave = obj.getString("mustHave") == "true" //todo поправить этот параметр
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