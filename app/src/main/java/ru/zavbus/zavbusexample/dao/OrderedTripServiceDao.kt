package ru.zavbus.zavbusexample.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import ru.zavbus.zavbusexample.entities.OrderedService
import ru.zavbus.zavbusexample.entities.TripService

@Dao
interface OrderedTripServiceDao {

    @Query("SELECT * FROM ordered_services WHERE id = :id")
    fun get(id: Long): OrderedService

    @Query("SELECT * from ordered_services")
    fun getAll(): List<OrderedService>

    @Insert(onConflict = REPLACE)
    fun insert(service: OrderedService)

    @Update
    fun update(service: OrderedService)

    @Query("DELETE from ordered_services")
    fun deleteAll()

    @Query("SELECT * FROM ordered_services WHERE tripRecordId = :recId AND tripServiceId = :serviceId")
    fun getAll(recId: Long, serviceId: Long): Array<OrderedService>

    @Query("SELECT * FROM trip_services ts, ordered_services os WHERE os.tripRecordId = :recId AND os.tripServiceId = ts.id")
    fun getAllServices(recId: Long): Array<TripService>

    @Query("""
        SELECT * FROM trip_services ts, ordered_services os, trip_records rec
        WHERE
            os.tripRecordId = rec.id AND
            os.tripServiceId = ts.id AND
            rec.id = :recId AND
            ts.tripPacketId = :packetId
    """)
    fun getAllOrderedServicesForRecordAndPacket(recId: Long, packetId: Long): Array<TripService>

    @Query("""
        SELECT * FROM    trip_records rec, ordered_services os
        WHERE
        rec.tripId = :tripId AND
        rec.confirmed = 1 AND
        os.tripRecordId = rec.id AND
        os.tripServiceId = :tripServiceId
    """)
    fun getAllOrderedServices(tripId: Long, tripServiceId: Long): Array<OrderedService>

    //удаляем все сервисы, которые выбраны в других пакетах
    @Query("DELETE FROM ordered_services WHERE tripRecordId = :recId AND tripServiceId NOT IN (:services)")
    fun deleteAllNotInServiceList(recId: Long, services: List<Long>)

    @Query("DELETE FROM ordered_services WHERE tripRecordId = :recId AND tripServiceId = :serviceId")
    fun deleteAll(recId: Long, serviceId: Long)

    @Query("DELETE FROM ordered_services WHERE tripRecordId = :recId")
    fun deleteAllForRecord(recId: Long)

}