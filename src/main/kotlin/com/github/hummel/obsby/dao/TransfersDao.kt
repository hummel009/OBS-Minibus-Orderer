package com.github.hummel.obsby.dao

import com.github.hummel.obsby.bean.TransfersInfo
import com.github.hummel.obsby.gson
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity

object TransfersDao {
	fun getBetweenCities(
		phone: String, date: String, cityFromId: String, cityToId: String
	): Array<TransfersInfo> {
		HttpClients.createDefault().use {
			val request = HttpPost("https://api.obs.by/transfers/betweenCities")
			val payload = """
			{
				"from": "$cityFromId",
				"to": "$cityToId",
				"date": "$date",
				"client": "$phone"
			}
			"""

			request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

			return@getBetweenCities it.execute(request) { response ->
				val entity = response.entity

				val stringResponse = EntityUtils.toString(entity)

				gson.fromJson(stringResponse, Array<TransfersInfo>::class.java)
			}
		}
	}
}