package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.TransfersDao

object TransfersService {
	fun getTimes(cache: Cache, phone: String, date: String, cityFromName: String, cityToName: String): Array<String> {
		var cityInfo = cache.citiesInfo.find {
			it.from.name == cityFromName
		}!!
		var cityFromId = cityInfo.from.id
		var cityToId = cityInfo.to.find {
			it.name == cityToName
		}!!.id

		cache.transfersInfo = TransfersDao.getBetweenCities(phone, date, cityFromId, cityToId)

		val times = cache.transfersInfo.map {
			it.from.time
		}.toTypedArray()

		times.sort()

		return times
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