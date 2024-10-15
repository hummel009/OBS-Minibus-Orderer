package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.CitiesDao

object CitiesService {
	fun getCitiesFromNames(cache: Cache): Array<String> {
		return try {
			cache.citiesInfo = CitiesDao.getForBooking()

			val citiesFromNames = cache.citiesInfo.map {
				it.from.name
			}.toTypedArray()

			if (citiesFromNames.isEmpty()) {
				throw Exception()
			}

			citiesFromNames.sort()

			citiesFromNames
		} catch (e: Exception) {
			e.printStackTrace()

			arrayOf("Минск", "Логойск")
		}
	}

	fun getCitiesToNames(cache: Cache, cityFromName: String): Array<String> {
		return try {
			val citiesToNames = cache.citiesInfo.find {
				it.from.name == cityFromName
			}!!.to.map {
				it.name
			}.toTypedArray()

			if (citiesToNames.isEmpty()) {
				throw Exception()
			}

			citiesToNames.sort()

			citiesToNames
		} catch (e: Exception) {
			e.printStackTrace()

			arrayOf("Минск", "Логойск")
		}
	}
}