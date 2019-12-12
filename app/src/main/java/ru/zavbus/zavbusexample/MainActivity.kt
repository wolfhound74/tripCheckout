package ru.zavbus.zavbusexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ListView
import ru.zavbus.zavbusexample.adapter.TripAdapter
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.services.InitDataService
import ru.zavbus.zavbusexample.utils.CustomModal
import ru.zavbus.zavbusexample.utils.ToastMessage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("Выезды")

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val db = ZavbusDb.getInstance(applicationContext)

        val listView = findViewById<ListView>(R.id.listView)

        val adapter = getAdapter(db?.tripDao()?.getAll()!!)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val trip = parent.getAdapter().getItem(position) as Trip

            val intent = Intent(this, TripActivity::class.java)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }
    }

    private fun getAdapter(trips: List<Trip>): TripAdapter {
        return TripAdapter(this, trips)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.downloadData -> {
            CustomModal().initSubmitDialog(this, "Данные с сервера", "Скачать данные с сервера? Текущие данные на этом устройстве будут перезаписаны!", {
                val url = resources.getString(R.string.url)
                val username = resources.getString(R.string.username)
                val password = resources.getString(R.string.password)

                try {
                    InitDataService(this, findViewById(R.id.listView))
                            .AsyncTaskHandler()
                            .execute(url + "/curatorData?username=" + username + "&password=" + password)
                } catch (e: Exception) {
                    ToastMessage().init(this@MainActivity, "Чет печаль вышла :(", false)
                }
            })
            true
        }
        R.id.removeData -> {
            CustomModal().initSubmitDialog(this, "Данные с сервера", "Удалить? Текущие данные на этом устройстве будут полностью удалены!", {
                Thread {
                    ZavbusDb.getInstance(applicationContext)?.tripDao()?.deleteAll()
                }.start()

                ToastMessage().init(this@MainActivity, "Данные удалены")
            })
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
