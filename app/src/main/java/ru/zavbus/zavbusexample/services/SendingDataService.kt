package ru.zavbus.zavbusexample.services

import android.content.Context
import android.os.AsyncTask
import org.json.JSONArray
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import java.net.HttpURLConnection
import java.net.URL


class SendingDataService(val applicationContext: Context) {

    inner class AsyncTaskHandler : AsyncTask<Trip, Void, Void>() {
        override fun doInBackground(vararg params: Trip?): Void? {
            val db = ZavbusDb.getInstance(applicationContext)
            val trip = params[0]

            val recs = db?.tripRecordDao()?.getAllConfirmedRecords(trip?.id!!)?.map {
                mapOf("i" to it.recordId, "s" to it.paidSumInBus)
            }
            httpPost(JSONArray(recs))

            return null
        }
    }

    private fun httpPost(arr: JSONArray): String {

        val text: String
        val connection = URL("")
                .openConnection() as HttpURLConnection

        try {
            connection.connect()
            text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
        } finally {
            connection.disconnect()
        }
        return text
    }

}