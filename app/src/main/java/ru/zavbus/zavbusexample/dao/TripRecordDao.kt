package ru.zavbus.zavbusexample.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import ru.zavbus.zavbusexample.entities.TripRecord
import ru.zavbus.zavbusexample.utils.DateConverter

@Dao
interface TripRecordDao {

    @Query("SELECT * from trip_records")
    fun getAll(): List<TripRecord>

    @Insert(onConflict = REPLACE)
    fun insert(record: TripRecord)

    @Update
    fun update(record: TripRecord)

    @Query("DELETE from trip_records")
    fun deleteAll()

    @Query("SELECT * FROM trip_records WHERE tripId = :tripId ORDER BY name")
    fun getRecordsByTrip(tripId: Long): Array<TripRecord>
}