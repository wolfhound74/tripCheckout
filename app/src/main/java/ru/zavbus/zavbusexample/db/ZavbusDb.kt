package ru.zavbus.zavbusexample.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.os.AsyncTask
import ru.zavbus.zavbusexample.dao.*
import ru.zavbus.zavbusexample.entities.*
import ru.zavbus.zavbusexample.utils.DateConverter

@Database(entities = arrayOf(
        TripRecord::class,
        Trip::class,
        TripPacket::class,
        TripService::class,
        OrderedService::class
), version = 16)
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
    abstract fun tripServiceDao(): TripServiceDao
    abstract fun orderedTripServiceDao(): OrderedTripServiceDao

    private class PopulateDbAsync(instance: ZavbusDb) : AsyncTask<Void, Void, Void>() {
        private val tripRecordDao: TripRecordDao
        private val tripDao: TripDao
        private val tripPacketDao: TripPacketDao
        private val tripServiceDao: TripServiceDao
        private val orderedTripServiceDao: OrderedTripServiceDao

        init {
            tripRecordDao = instance.tripRecordDao()
            tripDao = instance.tripDao()
            tripPacketDao = instance.tripPacketDao()
            tripServiceDao = instance.tripServiceDao()
            orderedTripServiceDao = instance.orderedTripServiceDao()
        }

        override fun doInBackground(vararg voids: Void): Void? {
//            tripRecordDao.deleteAll()
            return null
        }
    }

    companion object {
        private var INSTANCE: ZavbusDb? = null
        private val DB_NAME = "zavbus16.db"
        fun getInstance(context: Context): ZavbusDb? {
            if (INSTANCE == null) {
                synchronized(ZavbusDb::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, ZavbusDb::class.java, DB_NAME)
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