package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.Cache
import com.github.hummel.shuttle.dao.CitiesDao

object CitiesService {
	fun getCitiesFromNames(cache: Cache): Array<String> {
		return try {
			cache.citiesInfo = CitiesDao.getForBooking()

			val citiesFromNames = cache.citiesInfo.map {
				it.from.name
			}.toMutableList()

			if (citiesFromNames.isEmpty()) {
				throw Exception()
			}

			citiesFromNames.remove("Минск")
			citiesFromNames.remove("Логойск")

			arrayOf("Логойск", "Минск") + citiesFromNames.toTypedArray().sortedArray()
		} catch (e: Exception) {
			e.printStackTrace()

			arrayOf("Логойск", "Минск")
		}
	}

	fun getCitiesToNames(cache: Cache, cityFromName: String): Array<String> {
		return try {
			val citiesToNames = cache.citiesInfo.find {
				it.from.name == cityFromName
			}!!.to.map {
				it.name
			}.toMutableList()

			if (citiesToNames.isEmpty()) {
				throw Exception()
			}

			citiesToNames.remove("Минск")
			citiesToNames.remove("Логойск")

			arrayOf("Логойск", "Минск") + citiesToNames.toTypedArray().sortedArray()
		} catch (e: Exception) {
			e.printStackTrace()

			arrayOf("Логойск", "Минск")
		}
	}
}