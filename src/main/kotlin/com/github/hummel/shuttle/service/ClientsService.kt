package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.ClientsDao

object ClientsService {
	fun unlock(phone: String) {
		ClientsDao.optionsWithReservations(phone)
	}

	fun isTicketNotOrdered(
		cache: Cache, phone: String, token: String, date: String, time: String, stopFromName: String
	): Boolean {
		val clientInfo = ClientsDao.getWithReservations(phone, token)
		val realTime = cache.transfersInfo.find {
			it.from.time == time
		}!!.stopsForBooking.find {
			it.from.name == stopFromName
		}!!.from.time

		return clientInfo.reservations.none {
			it.date == date && it.from.time == realTime
		}
	}
}