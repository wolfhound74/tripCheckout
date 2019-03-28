package ru.zavbus.zavbusexample.entities

import android.arch.persistence.room.*

@Entity(tableName = "trip_records",
        indices = arrayOf(Index(value = ["tripId"], name = "tripId")),
        foreignKeys = arrayOf(
                ForeignKey(entity = Trip::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("tripId"),
                        onDelete = ForeignKey.CASCADE),
                ForeignKey(entity = TripPacket::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("packetId"))
        ))
data class TripRecord(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "tripId") val tripId: Long,
        @ColumnInfo(name = "mainRiderId") val mainRiderId: Long?,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "commentFromVk") val commentFromVk: String?,
        @ColumnInfo(name = "orderedKit") val orderedKit: String?,
        @ColumnInfo(name = "prepaidSum") val prepaidSum: Long?,
        @ColumnInfo(name = "packetId") val packetId: Long,
        @ColumnInfo(name = "phone") val phone: String?
) : java.io.Serializable {
    override fun toString(): String {
        return name
    }
}