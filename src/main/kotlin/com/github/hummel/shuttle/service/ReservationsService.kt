package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.ReservationsDao
import com.github.hummel.shuttle.dao.TransfersDao

object ReservationsService {
	fun postBook(
		cache: Cache,
		phone: String,
		token: String,
		date: String,
		cityFromName: String,
		cityToName: String,
		time: String,
		stopFromName: String,
		stopToName: String
	) {
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

		try {
			val transferInfo = transfersInfo.find {
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
		} catch (_: Exception) {
			println("[Заказ билета] Расписание на эту дату недоступно. Ожидание реальных данных.")
		}
	}
}