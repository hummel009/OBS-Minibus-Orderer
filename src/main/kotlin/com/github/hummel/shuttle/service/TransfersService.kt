package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.TransfersDao
import com.github.hummel.shuttle.formatter
import java.time.LocalDate

object TransfersService {
	fun getTimes(cache: Cache, phone: String, date: String, cityFromName: String, cityToName: String): Array<String> {
		val cityInfo = cache.citiesInfo.find {
			it.from.name == cityFromName
		}!!
		val cityFromId = cityInfo.from.id
		val cityToId = cityInfo.to.find {
			it.name == cityToName
		}!!.id

		return try {
			cache.transfersInfo = TransfersDao.getBetweenCities(phone, date, cityFromId, cityToId)

			val times = cache.transfersInfo.map {
				it.from.time
			}.toTypedArray()

			if (times.isEmpty()) {
				throw Exception()
			}

			times.sort()

			times
		} catch (_: Exception) {
			println("Расписание на эту дату недоступно. Загружены псевдо-данные завтрашнего дня.")

			val pseudoDate = LocalDate.now().plusDays(1).format(formatter)

			cache.transfersInfo = TransfersDao.getBetweenCities(phone, pseudoDate, cityFromId, cityToId)
			cache.transfersInfoPseudo = true

			val times = cache.transfersInfo.map {
				it.from.time
			}.toTypedArray()

			times.sort()

			times
		}
	}

	fun getStopsFromNames(cache: Cache, time: String): Array<String> {
		val stopsFromNames = cache.transfersInfo.find {
			it.from.time == time
		}!!.stopsForBooking.map {
			it.from.name
		}.toTypedArray()

		return stopsFromNames
	}

	fun getStopsToNames(cache: Cache, time: String, stopFromName: String): Array<String> {
		val stopsToNames = cache.transfersInfo.find {
			it.from.time == time
		}!!.stopsForBooking.find {
			it.from.name == stopFromName
		}!!.to.map {
			it.name
		}.toTypedArray()

		return stopsToNames
	}
}