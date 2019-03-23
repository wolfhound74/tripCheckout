package ru.zavbus.zavbusexample.entities

import android.arch.persistence.room.*

@Entity(tableName = "trip_records",
        indices = arrayOf(Index(value = ["tripId"], name = "tripId")),
        foreignKeys = arrayOf(ForeignKey(entity = Trip::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("tripId"),
                onDelete = ForeignKey.CASCADE)
        ))
data class TripRecord(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "tripId") val tripId: Long,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "phone") val phone: String
) : java.io.Serializable {
    override fun toString(): String {
        return name
    }
}