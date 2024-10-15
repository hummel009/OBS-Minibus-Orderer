package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.ReservationsDao
import kotlin.collections.find

object ReservationsService {
	fun postBook(cache: Cache, phone: String, token: String, time: String, stopFromName: String, stopToName: String) {
		val transferInfo = cache.transfersInfo.find {
			it.from.time == time
		}!!.stopsForBooking.find {
			it.from.name == stopFromName
		}!!

		val stopFromId = transferInfo.from.id
		val stopToId = transferInfo.to.find {
			it.name == stopToName
		}!!.id

		ReservationsDao.postBook(phone, token, time, stopFromId, stopToId)
	}
}