package ru.zavbus.zavbusexample.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.os.AsyncTask
import ru.zavbus.zavbusexample.dao.TripDao
import ru.zavbus.zavbusexample.dao.TripPacketDao
import ru.zavbus.zavbusexample.dao.TripRecordDao
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripPacket
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.utils.DateConverter

@Database(entities = arrayOf(TripRecord::class, Trip::class, TripPacket::class), version = 5)
@TypeConverters(value = arrayOf(DateConverter::class))
abstract class ZavbusDb : RoomDatabase() {
    fun clearDb() {
        if (INSTANCE != null) {
            PopulateDbAsync(INSTANCE!!).execute()
        }
    }

    abstract fun tripRecordDao(): TripRecordDao
    abstract fun tripDao(): TripDao
    abstract fun tripPacketDao(): TripPacketDao

    private class PopulateDbAsync(instance: ZavbusDb) : AsyncTask<Void, Void, Void>() {
        private val tripRecordDao: TripRecordDao
        private val tripDao: TripDao
        private val tripPacketDao: TripPacketDao

        init {
            tripRecordDao = instance.tripRecordDao()
            tripDao = instance.tripDao()
            tripPacketDao = instance.tripPacketDao()
        }

        override fun doInBackground(vararg voids: Void): Void? {
            tripRecordDao.deleteAll()
//            val directorOne = Director("Adam McKay")
//            val directorTwo = Director("Denis Villeneuve")
//            val directorThree = Director("Morten Tyldum")
//            val movieOne = Movie("The Big Short", directorDao.insert(directorOne) as Int)
//            val dIdTwo = directorDao.insert(directorTwo) as Int
//            val movieTwo = Movie("Arrival", dIdTwo)
//            val movieThree = Movie("Blade Runner 2049", dIdTwo)
//            val movieFour = Movie("Passengers", directorDao.insert(directorThree) as Int)
//            movieDao.insert(movieOne, movieTwo, movieThree, movieFour)
            return null
        }
    }

    companion object {
        private var INSTANCE: ZavbusDb? = null
        private val DB_NAME = "zavbus5.db"
        fun getInstance(context: Context): ZavbusDb? {
            if (INSTANCE == null) {
                synchronized(ZavbusDb::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                ZavbusDb::class.java, DB_NAME)
                                .allowMainThreadQueries() // SHOULD NOT BE USED IN PRODUCTION !!!
                                .addCallback(object : RoomDatabase.Callback() {
                                    override fun onCreate(db: SupportSQLiteDatabase) {
                                        super.onCreate(db)
                                        PopulateDbAsync(INSTANCE!!).execute()
                                    }
                                })
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}