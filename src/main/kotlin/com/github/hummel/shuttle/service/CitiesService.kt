package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.CitiesDao

object CitiesService {
	fun getCitiesFromNames(cache: Cache): Array<String> {
		cache.citiesInfo = CitiesDao.getForBooking()

		val citiesNames = cache.citiesInfo.map {
			it.from.name
		}.toTypedArray()

		citiesNames.sort()

		return citiesNames
	}

	fun getCitiesToNames(cache: Cache, cityFromName: String): Array<String> {
		val citiesNames = cache.citiesInfo.find {
			it.from.name == cityFromName
		}!!.to.map {
			it.name
		}.toTypedArray()

		citiesNames.sort()

		return citiesNames
	}
}