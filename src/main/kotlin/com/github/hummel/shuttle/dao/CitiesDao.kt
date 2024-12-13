package com.github.hummel.shuttle.dao

import com.github.hummel.shuttle.bean.CitiesInfo
import com.github.hummel.shuttle.gson
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils

object CitiesDao {
	fun getForBooking(): Array<CitiesInfo> {
		HttpClients.createDefault().use {
			val request = HttpGet("https://api.obs.by/cities/forBooking")

			return@getForBooking it.execute(request) { response ->
				val entity = response.entity

				val stringResponse = EntityUtils.toString(entity)

				gson.fromJson(stringResponse, Array<CitiesInfo>::class.java)
			}
		}
	}
}