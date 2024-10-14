package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.dao.ClientsDao

object ClientsService {
	fun unlock(phone: String) {
		ClientsDao.optionsWithReservations(phone)
	}

	fun getIsTicketNotOrdered(
		phone: String, token: String, date: String, stopFromName: String, time: String
	): Boolean {
		val clientInfo = ClientsDao.getWithReservations(phone, token)

		return clientInfo.reservations.none {
			it.from.name == stopFromName && it.from.time == time && it.date == date
		}
	}
}