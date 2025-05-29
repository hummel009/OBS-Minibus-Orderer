package com.github.hummel.obsby.service

import com.github.hummel.obsby.Cache
import com.github.hummel.obsby.dao.CitiesDao

object CitiesService {
	fun getCitiesFromNames(
		cache: Cache
	): Array<String> {
		return try {
			cache.citiesInfo = CitiesDao.getForBooking()

			val citiesFromNames = cache.citiesInfo.map {
				it.from.name
			}.toSortedSet().toMutableList()

			if (citiesFromNames.isEmpty()) {
				throw Exception()
			}

			citiesFromNames.indexOf("Логойск").takeIf { it != -1 }?.let {
				citiesFromNames.add(0, citiesFromNames.removeAt(it))
			}

			citiesFromNames.indexOf("Минск").takeIf { it != -1 }?.let {
				citiesFromNames.add(0, citiesFromNames.removeAt(it))
			}

			citiesFromNames.toTypedArray()
		} catch (e: Exception) {
			e.printStackTrace()

			arrayOf("Логойск", "Минск")
		}
	}

	fun getCitiesToNames(
		cache: Cache, cityFromName: String
	): Array<String> {
		return try {
			val citiesToNames = cache.citiesInfo.find {
				it.from.name == cityFromName
			}!!.to.map {
				it.name
			}.toSortedSet().toMutableList()

			if (citiesToNames.isEmpty()) {
				throw Exception()
			}

			citiesToNames.indexOf("Логойск").takeIf { it != -1 }?.let {
				citiesToNames.add(0, citiesToNames.removeAt(it))
			}

			citiesToNames.indexOf("Минск").takeIf { it != -1 }?.let {
				citiesToNames.add(0, citiesToNames.removeAt(it))
			}

			citiesToNames.toTypedArray()
		} catch (e: Exception) {
			e.printStackTrace()

			arrayOf("Логойск", "Минск")
		}
	}

	fun getCityIdsByNames(
		cache: Cache, cityFromName: String, cityToName: String
	): Pair<String, String> {
		val cityInfo = cache.citiesInfo.find {
			it.from.name == cityFromName
		}!!

		val cityFromId = cityInfo.from.id
		val cityToId = cityInfo.to.find {
			it.name == cityToName
		}!!.id

		return cityFromId to cityToId
	}
}