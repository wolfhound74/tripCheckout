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

    @Query("DELETE FROM ordered_services WHERE tripRecordId = :recId AND tripServiceId = :serviceId")
    fun deleteAll(recId: Long, serviceId: Long)

}