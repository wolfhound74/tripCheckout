package ru.zavbus.zavbusexample.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.utils.DateConverter

@Dao
@TypeConverters(value = arrayOf(DateConverter::class))
interface TripDao {

    @Query("SELECT * from trips")
    fun getAll(): List<Trip>

    @Insert(onConflict = REPLACE)
    fun insert(trip: Trip)

    @Update
    fun update(trip: Trip)

    @Query("DELETE from trips")
    fun deleteAll()
}