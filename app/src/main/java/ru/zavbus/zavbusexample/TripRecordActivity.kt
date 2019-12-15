package ru.zavbus.zavbusexample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import ru.zavbus.zavbusexample.adapter.TripServiceAdapter
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripPacket
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.entities.TripService
import ru.zavbus.zavbusexample.services.PriceService

class TripRecordActivity : AppCompatActivity() {

    private var currentPacket: TripPacket? = null
    private var tripRecord: TripRecord? = null
    private var trip: Trip? = null
    private var plusOneTripRecordIds: ArrayList<Long>? = null
    private val db = ZavbusDb.getInstance(this)

    val priceService: PriceService = PriceService(this)

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureActivity()

        initCommentFromVk()
        initOrderedKit()

        initPacketsSelector()

        initPrepaidSumBlock()
        initDiscountBlock()
        initMoneyBackBlock()

        initConfirmTripRecordListener()
        initCancelTripRecordListener()
        initPlusOneTripRecordListener()

        toggleConfirmButton(tripRecord?.confirmed!!)
    }

    private fun initPacketsSelector() {
        val packets = db?.tripPacketDao()?.getPacketsByTrip(trip!!.id)
        val selectedPacket = db?.tripPacketDao()?.get(tripRecord!!.packetId)

        val spinner: Spinner = findViewById(R.id.packets)
        ArrayAdapter<TripPacket>(this, R.layout.spinner_layout, packets).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        if (packets != null) {
            spinner.setSelection(packets.indexOf(selectedPacket))
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                val packet = packets?.get(position)
                currentPacket = packet
                packet?.let { initServices(it, tripRecord!!) }
                CountResultTask().execute(tripRecord)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    private fun initPrepaidSumBlock() {
        val prepaidSumBlock = findViewById<LinearLayout>(R.id.prepaidSumBlock)
        val prepaidSum = prepaidSumBlock.findViewById<TextView>(R.id.prepaidSum)
        prepaidSum.inputType = InputType.TYPE_NULL

        if (tripRecord?.prepaidSum!! > 0) {
            prepaidSum.text = tripRecord!!.prepaidSum.toString()
        } else {
            prepaidSumBlock.setVisibility(View.GONE)
        }
    }

    private fun initOrderedKit() {
        val orderedKit = findViewById<TextView>(R.id.orderedKit)
        if (tripRecord?.orderedKit!!.isEmpty()) {
            orderedKit.setVisibility(View.GONE)
        } else {
            orderedKit.text = tripRecord?.orderedKit
        }
    }

    private fun initCommentFromVk() {
        val commentFromVk = findViewById<TextView>(R.id.commentFromVk)
        if (tripRecord?.commentFromVk!!.isEmpty()) {
            commentFromVk.setVisibility(View.GONE)
        } else {
            commentFromVk.text = tripRecord?.commentFromVk
        }
    }

    private fun initMoneyBackBlock() {
        val moneyBack: TextView = findViewById(R.id.moneyBack)
        moneyBack.text = tripRecord?.moneyBack.toString()

        moneyBack.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val moneyBackSum: Int = if (s.isEmpty()) 0 else Integer.parseInt(s.toString())
                tripRecord?.moneyBack = moneyBackSum
            }
        })
    }

    private fun initDiscountBlock() {
        val discountTextView: TextView = findViewById(R.id.discount)
        discountTextView.text = tripRecord?.discountSum.toString()

        discountTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val discountSum: Int = if (s.isEmpty()) 0 else Integer.parseInt(s.toString())
                tripRecord?.discountSum = discountSum

                CountResultTask().execute(tripRecord)
            }
        })
    }

    fun initServices(packet: TripPacket, tripRecord: TripRecord) {
        val services = db?.tripServiceDao()?.getServicesByPacket(packet.id)?.toCollection(ArrayList()) as ArrayList<TripService>
        val adapter = TripServiceAdapter(this, services, db, tripRecord)
        findViewById<ListView>(R.id.services).adapter = adapter
    }

    inner class CountResultTask : AsyncTask<TripRecord, String, TripRecord>() {
        val resultSumText: TextView = findViewById(R.id.resultSumText)

        override fun doInBackground(vararg params: TripRecord): TripRecord {
            return params[0]
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(tripReocrd: TripRecord) {
            super.onPostExecute(tripReocrd)

            val sum = priceService.requiredSum(tripRecord!!, currentPacket!!.id)
            resultSumText.text = "$sum \u20BD"
        }
    }

    inner class ConfirmTask : AsyncTask<Boolean, Void, Void>() {
        override fun doInBackground(vararg params: Boolean?): Void? {
            saveTripRecord(tripRecord)

            tripRecord?.confirmed = params[0]

            db?.tripRecordDao()?.update(tripRecord!!)

            return null
        }
    }

    inner class SaveRecordTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            saveTripRecord(tripRecord)
            return null
        }
    }

    private fun saveTripRecord(tr: TripRecord?) {
        tr!!.packetId = currentPacket!!.id
        val servicesInPacket = db?.tripServiceDao()?.getServicesByPacket(currentPacket!!.id)?.map { it.id } as ArrayList<Long>
        db.orderedTripServiceDao().deleteAllNotInServiceList(tr.id, servicesInPacket)
        tr.paidSumInBus = priceService.requiredSum(tr, tr.packetId)
        db.tripRecordDao().update(tr)
    }


    fun initConfirmTripRecordListener(): Void? {
        val btn: Button = findViewById(R.id.confirmTripRecordButton)

        if (plusOneTripRecordIds?.size!! > 0) {
            btn.visibility = View.GONE
        }

        btn.setOnClickListener {
            ConfirmTask().execute(true)
            val intent = Intent(this, TripRecordListActivity::class.java)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }
        return null
    }

    fun toggleConfirmButton(confirmed: Boolean) {
        val confirmBlock: LinearLayout = findViewById(R.id.confirmBlock)
        val cancelBlock: LinearLayout = findViewById(R.id.cancelBlock)

        if (confirmed) {
            confirmBlock.visibility = View.GONE
            cancelBlock.visibility = View.VISIBLE
        } else {
            confirmBlock.visibility = View.VISIBLE
            cancelBlock.visibility = View.GONE
        }
    }

    fun initCancelTripRecordListener(): Void? {
        val btn: Button = findViewById(R.id.cancelTripRecordButton)

        btn.setOnClickListener {
            ConfirmTask().execute(false)
            val intent = Intent(this, TripRecordListActivity::class.java)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }
        return null
    }

    fun initPlusOneTripRecordListener(): Void? {
        val btn: Button = findViewById(R.id.plusOne)

        btn.setOnClickListener {
            SaveRecordTask().execute()

            val intent = Intent(this, PlusOneTripRecordsActivity::class.java)

            plusOneTripRecordIds?.add(tripRecord!!.id)

            intent.putExtra("plusOneTripRecordIds", plusOneTripRecordIds)
            intent.putExtra("trip", trip)

            startActivity(intent)
        }
        return null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, TripRecordListActivity::class.java)
        myIntent.putExtra("trip", trip)
        startActivityForResult(myIntent, 0)
        return true
    }


    private fun configureActivity() {
        setContentView(R.layout.activity_trip_record)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        tripRecord = intent.getSerializableExtra("tripRecord") as TripRecord
        trip = intent.getSerializableExtra("trip") as Trip

        plusOneTripRecordIds = intent.getSerializableExtra("plusOneTripRecordIds") as ArrayList<Long>?
                ?: arrayListOf<Long>()

        title = tripRecord!!.name
    }
}
