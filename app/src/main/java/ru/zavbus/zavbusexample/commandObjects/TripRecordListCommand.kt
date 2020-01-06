package ru.zavbus.zavbusexample.commandObjects

import ru.zavbus.zavbusexample.entities.Trip
import java.io.Serializable

class TripRecordListCommand(
        val trip: Trip,
        val plusOneTripRecordIds: ArrayList<Long> = arrayListOf<Long>()
) : Serializable