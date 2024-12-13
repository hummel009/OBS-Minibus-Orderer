package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.ClientsDao
import com.github.hummel.shuttle.dao.TransfersDao

object ClientsService {
	fun unlock(phone: String) {
		try {
			ClientsDao.optionsWithReservations(phone)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	fun isTicketNotOrdered(
		cache: Cache,
		phone: String,
		token: String,
		date: String,
		cityFromName: String,
		cityToName: String,
		time: String,
		stopFromName: String
	): Boolean {
		return try {
			val clientInfo = ClientsDao.getWithReservations(phone, token)

			val transfersInfo = if (!cache.transfersInfoPseudo) {
				cache.transfersInfo
			} else {
				val cityInfo = cache.citiesInfo.find {
					it.from.name == cityFromName
				}!!
				val cityFromId = cityInfo.from.id
				val cityToId = cityInfo.to.find {
					it.name == cityToName
				}!!.id

				cache.transfersInfo = TransfersDao.getBetweenCities(phone, date, cityFromId, cityToId)

				if (cache.transfersInfo.isEmpty()) {
					throw Exception()
				}

				cache.transfersInfoPseudo = false
				cache.transfersInfo
			}

			val realTime = transfersInfo.find {
				it.from.time == time
			}!!.stopsForBooking.find {
				it.from.name == stopFromName
			}!!.from.time

			return clientInfo.reservations.none {
				it.date == date && it.from.time == realTime
			}
		} catch (_: Exception) {
			println("[Проверка наличия взятого билета] Расписание на эту дату недоступно. Ожидание реальных данных.")

			true
		}
	}
}