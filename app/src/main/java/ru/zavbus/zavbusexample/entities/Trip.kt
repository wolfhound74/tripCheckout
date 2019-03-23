package ru.zavbus.zavbusexample.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "trips")
data class Trip(
        @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "trip_dates") var tripDates: String
) : java.io.Serializable {
    override fun toString(): String {
        return "$tripDates $name"
    }
}