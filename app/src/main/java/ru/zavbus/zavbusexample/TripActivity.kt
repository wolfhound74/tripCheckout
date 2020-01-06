package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ListView
import ru.zavbus.zavbusexample.adapter.TripInfoAdapter
import ru.zavbus.zavbusexample.commandObjects.TripRecordListCommand
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripService
import ru.zavbus.zavbusexample.services.SendingDataService
import ru.zavbus.zavbusexample.utils.CustomModal
import ru.zavbus.zavbusexample.utils.ToastMessage
import java.io.Serializable

class TripActivity : AppCompatActivity() {

    private var trip: Trip? = null
    private val db = ZavbusDb.getInstance(this)
    private val actions: MutableList<Map<String, Any>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureActivity()

        initActions()

        initTripInfo()
    }

    private fun initTripInfo() {
        val listActions = findViewById<ListView>(R.id.tripActions)
        val adapter = TripInfoAdapter(this, actions)

        listActions.adapter = adapter
        listActions.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    doStartActivity(TripRecordListActivity::class.java, TripRecordListCommand(trip!!))
                }
                else -> {
                    val action = actions[position]
                    if (action.containsKey("service")) {
                        doStartActivity(TripRecordListActivity::class.java, TripRecordListCommand(trip!!, action["service"] as TripService?))
                    }
                }
            }
        }
    }

    private fun doStartActivity(cls: Class<*>, cmd: Serializable) {
        val intent = Intent(this, cls)
        intent.putExtra("cmd", cmd)
        startActivity(intent)
    }

    private fun initActions(): MutableList<Map<String, Any>> {
        actions.add(HashMap(
                hashMapOf("action" to "Участники выезда", "info" to "⟩")
        ))

        val services = db?.tripServiceDao()?.getServicesForTrip(trip?.id!!)?.distinctBy { it.serviceId }
        services?.forEach { s ->
            val orderedServices = db?.orderedTripServiceDao()?.getAllOrderedServices(trip?.id!!, s.serviceId)

            if (orderedServices?.size!! > 0) {
                actions.add(HashMap(
                        hashMapOf("action" to s.name, "info" to "" + orderedServices.size, "service" to s)
                ))
            }
        }

        val records = db?.tripRecordDao()?.getAllConfirmedRecords(trip!!.id)
        val paidSumInBus = db?.tripRecordDao()?.getAllPaidSumInBus(trip!!.id)
        val totalMoneyBack = db?.tripRecordDao()?.getTotalMoneyBack(trip!!.id)

        actions.add(HashMap(hashMapOf("action" to "Участники", "info" to "" + records?.size)))
        actions.add(HashMap(hashMapOf("action" to "Денег собрано", "info" to "" + paidSumInBus + " \u20BD")))
        actions.add(HashMap(hashMapOf("action" to "Сдача", "info" to "" + totalMoneyBack + " \u20BD")))

        return actions
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.trip_actions, menu)

        if (trip?.note!!.isEmpty()) {
            menu.findItem(R.id.tripNote).setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.sendData -> {
            try {
                val url = resources.getString(R.string.url)

                CustomModal().initSubmitDialog(this,
                        "Отправка данных",
                        "Отправить данные на $url? Текущие данные по этому выезду будут отправлены на сервер!",
                        { SendingDataService(this).AsyncTaskHandler().execute(trip) }
                )
            } catch (e: Exception) {
                ToastMessage().init(this, "Чет печаль вышла :(", false)
            }
            true
        }
        R.id.tripNote -> {
            CustomModal().initInfoDialog(this, trip?.note!!, "Заметка к выезду")
            true
        }
        android.R.id.home -> {
            backToMainActivity()
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

    private fun backToMainActivity() {
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(myIntent, 0)
    }

    override fun onBackPressed() {
        backToMainActivity()
    }

}

