package ru.zavbus.zavbusexample.services

import android.content.Context
import android.os.AsyncTask
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.json.JSONArray
import ru.zavbus.zavbusexample.R
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip


class SendingDataService(val applicationContext: Context) {

    inner class AsyncTaskHandler : AsyncTask<Trip, Void, Void>() {
        override fun doInBackground(vararg params: Trip?): Void? {
            val db = ZavbusDb.getInstance(applicationContext)
            val trip = params[0]

            val recs = db?.tripRecordDao()?.getAllRecords(trip?.id!!)?.map {
                mapOf(
                        "i" to it.recordId,
                        "s" to it.paidSumInBus,
                        "c" to it.confirmed
                )
            }
            httpPost(JSONArray(recs))

            return null
        }
    }

    private fun httpPost(arr: JSONArray): String {
        val url = applicationContext.getString(R.string.url)
        val username = applicationContext.getString(R.string.username)
        val password = applicationContext.getString(R.string.password)

        //todo обернуть в try-catch
        val path = url + "/updateTripRecords?username=" + username + "&password=" + password
        val post = HttpPost(path)
        post.entity = StringEntity(arr.toString())
        post.setHeader("Content-type", "application/json")

        HttpClientBuilder.create().build().execute(post)

        return ""
    }

}