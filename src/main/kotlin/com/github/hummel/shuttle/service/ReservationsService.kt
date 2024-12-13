package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.ReservationsDao

object ReservationsService {
	fun postBook(
		cache: Cache,
		phone: String,
		token: String,
		time: String,
		stopFromName: String,
		stopToName: String
	) {
		val transferInfo = cache.transfersInfo.find {
			it.from.time == time
		}!!
		val transferId = transferInfo.id

		val stopForBooking = transferInfo.stopsForBooking.find {
			it.from.name == stopFromName
		}!!
		val stopFromId = stopForBooking.from.id
		val stopToId = stopForBooking.to.find {
			it.name == stopToName
		}!!.id

		ReservationsDao.postBook(phone, token, transferId, stopFromId, stopToId)
	}
}