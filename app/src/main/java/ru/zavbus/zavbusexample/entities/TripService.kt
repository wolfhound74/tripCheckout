package ru.zavbus.zavbusexample.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "trip_services",
        foreignKeys = arrayOf(ForeignKey(entity = TripPacket::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("tripPacketId"),
                onDelete = ForeignKey.CASCADE)
        ))
data class TripService(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "tripPacketId") val tripPacketId: Long,
        @ColumnInfo(name = "serviceId") val serviceId: Long,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "mustHave") val mustHave: Boolean,
        @ColumnInfo(name = "price") val price: Int
) : java.io.Serializable {
    override fun toString(): String {
        return name
    }
}