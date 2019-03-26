package ru.zavbus.zavbusexample.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "ordered_services", foreignKeys = arrayOf(
        ForeignKey(entity = TripRecord::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("tripRecordId"),
                onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TripService::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("tripServiceId"),
                onDelete = ForeignKey.CASCADE)
))
data class OrderedService(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "tripRecordId") val tripRecordId: Long,
        @ColumnInfo(name = "tripServiceId") val tripServiceId: Long
) : java.io.Serializable