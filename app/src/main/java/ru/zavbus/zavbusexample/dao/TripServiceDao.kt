package ru.zavbus.zavbusexample.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import ru.zavbus.zavbusexample.entities.TripService

@Dao
interface TripServiceDao {

    @Query("SELECT * from trip_services")
    fun getAll(): List<TripService>

    @Insert(onConflict = REPLACE)
    fun insert(service: TripService)

    @Update
    fun update(service: TripService)

    @Query("DELETE from trip_records")
    fun deleteAll()

    @Query("SELECT * FROM trip_services WHERE tripPacketId = :tripPacketId ORDER BY mustHave")
    fun getServicesByPacket(tripPacketId: Long): Array<TripService>

    @Query("""
        SELECT * FROM trip_packets p, trip_services ts
        WHERE
             p.tripId = :tripId AND
             ts.tripPacketId = p.id

        """)
    fun getServicesForTrip(tripId: Long): Array<TripService>
}