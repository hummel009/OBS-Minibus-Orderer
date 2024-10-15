package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.ReservationsDao
import com.github.hummel.shuttle.dao.TransfersDao
import kotlin.collections.find

object ReservationsService {
	fun postBook(
		cache: Cache,
		phone: String,
		token: String,
		date: String,
		cityFromId: String,
		cityToId: String,
		time: String,
		stopFromName: String,
		stopToName: String
	) {
		val transfersInfo = if (!cache.transfersInfoPseudo) {
			cache.transfersInfo
		} else {
			TransfersDao.getBetweenCities(phone, date, cityFromId, cityToId)
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
			println("Расписание на эту дату недоступно. Ожидание реальных данных.")
		}
	}
}