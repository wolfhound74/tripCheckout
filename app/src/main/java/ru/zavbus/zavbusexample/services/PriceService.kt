package ru.zavbus.zavbusexample.services

import android.content.Context
import ru.zavbus.zavbusexample.db.ZavbusDb
import ru.zavbus.zavbusexample.entities.TripPacket
import ru.zavbus.zavbusexample.entities.TripRecord

class PriceService(applicationContext: Context) {
    private val db = ZavbusDb.getInstance(applicationContext)

    fun requiredSum(tripRecord: TripRecord, packetId: Long): Int {
        val discountSum = tripRecord.discountSum ?: 0
        return Math.max(getPrice(tripRecord, packetId) - tripRecord.prepaidSum!! - discountSum, 0)
    }

    private fun getPrice(tripRecord: TripRecord, packetId: Long): Int {
        val price = db?.orderedTripServiceDao()
                ?.getAllOrderedServicesForRecordAndPacket(tripRecord.id, packetId)
                ?.sumBy { it.price }

        return price!!
    }
}