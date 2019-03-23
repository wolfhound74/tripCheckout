package ru.zavbus.zavbusexample.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import ru.zavbus.zavbusexample.entities.TripPacket

@Dao
interface TripPacketDao {

    @Query("SELECT * from trip_packets")
    fun getAll(): List<TripPacket>

    @Insert(onConflict = REPLACE)
    fun insert(packet: TripPacket)

    @Update
    fun update(packet: TripPacket)

    @Query("DELETE from trip_packets")
    fun deleteAll()

    @Query("SELECT * FROM trip_packets WHERE tripId = :tripId")
    fun getPacketsByTrip(tripId: Long): Array<TripPacket>
}