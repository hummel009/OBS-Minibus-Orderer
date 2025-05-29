package com.github.hummel.obsmo.service

import com.github.hummel.obsmo.Cache
import com.github.hummel.obsmo.dao.ClientsDao
import com.github.hummel.obsmo.dao.TransfersDao

object ClientsService {
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
			ClientsDao.optionsWithReservations(phone)

			val clientInfo = ClientsDao.getWithReservations(phone, token)

			val transfersInfo = if (!cache.transfersInfoPseudo) cache.transfersInfo else {
				val (cityFromId, cityToId) = CitiesService.getCityIdsByNames(cache, cityFromName, cityToName)

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
			true
		}
	}
}