package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.CitiesDao

object CitiesService {
	fun getCitiesFromNames(cache: Cache): Array<String> {
		cache.citiesInfo = CitiesDao.getForBooking()

		val citiesFromNames = cache.citiesInfo.map {
			it.from.name
		}.toTypedArray()

		citiesFromNames.sort()

		return citiesFromNames
	}

	fun getCitiesToNames(cache: Cache, cityFromName: String): Array<String> {
		val citiesToNames = cache.citiesInfo.find {
			it.from.name == cityFromName
		}!!.to.map {
			it.name
		}.toTypedArray()

		citiesToNames.sort()

		return citiesToNames
	}
}