package ru.zavbus.zavbusexample.commandObjects

import ru.zavbus.zavbusexample.entities.Trip
import ru.zavbus.zavbusexample.entities.TripService
import java.io.Serializable

class TripRecordListCommand : Serializable {
    var trip: Trip
    var plusOneTripRecordIds: ArrayList<Long>
    var filteredByService: TripService?

    constructor(trip: Trip) {
        this.trip = trip
        this.plusOneTripRecordIds = arrayListOf<Long>()
        this.filteredByService = null
    }

    constructor(trip: Trip, plusOneTripRecordIds: ArrayList<Long>) {
        this.trip = trip
        this.plusOneTripRecordIds = plusOneTripRecordIds
        this.filteredByService = null
    }

    constructor(trip: Trip, tripService: TripService?) {
        this.trip = trip
        this.plusOneTripRecordIds = arrayListOf<Long>()
        this.filteredByService = tripService
    }

}