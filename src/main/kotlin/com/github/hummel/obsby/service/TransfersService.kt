package com.github.hummel.obsby.service

import com.github.hummel.obsby.Cache
import com.github.hummel.obsby.dao.TransfersDao
import com.github.hummel.obsby.formatter
import java.time.LocalDate

object TransfersService {
	fun getTimes(
		cache: Cache, phone: String, date: String, cityFromName: String, cityToName: String
	): Array<String> {
		val (cityFromId, cityToId) = CitiesService.getCityIdsByNames(cache, cityFromName, cityToName)

		return try {
			cache.transfersInfo = TransfersDao.getBetweenCities(phone, date, cityFromId, cityToId)

			val times = cache.transfersInfo.map {
				it.from.time
			}.toTypedArray()

			if (times.isEmpty()) {
				throw Exception()
			}

			times.sortedArray()
		} catch (_: Exception) {
			println("Расписание на эту дату недоступно. Загружены псевдо-данные (завтрашний день).")

			val pseudoDate = LocalDate.now().plusDays(1).format(formatter)

			cache.transfersInfo = TransfersDao.getBetweenCities(phone, pseudoDate, cityFromId, cityToId)
			cache.transfersInfoPseudo = true

			val times = cache.transfersInfo.map {
				it.from.time
			}.toTypedArray()

			times.sortedArray()
		}
	}

	fun getStopsFromNames(
		cache: Cache, time: String
	): Array<String> {
		val stopsFromNames = cache.transfersInfo.find {
			it.from.time == time
		}!!.stopsForBooking.map {
			it.from.name
		}.toTypedArray()

		return stopsFromNames
	}

	fun getStopsToNames(
		cache: Cache, time: String, stopFromName: String
	): Array<String> {
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