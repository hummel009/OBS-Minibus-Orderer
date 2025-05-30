package com.github.hummel.obsmo.dao

import com.github.hummel.obsmo.bean.ClientInfo
import com.github.hummel.obsmo.gson
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpOptions
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils

object ClientsDao {
	fun optionsWithReservations(
		phone: String
	) {
		HttpClients.createDefault().use {
			val request = HttpOptions("https://api.obs.by/clients/withReservations/$phone")

			request.addHeader("Access-Control-Request-Headers", "authorization")
			request.addHeader("Access-Control-Request-Method", "GET")

			it.execute(request) { }
		}
	}

	fun getWithReservations(
		phone: String, token: String
	): ClientInfo {
		HttpClients.createDefault().use {
			val request = HttpGet("https://api.obs.by/clients/withReservations/$phone")

			request.addHeader("Authorization", "Bearer $token")

			return@getWithReservations it.execute(request) { response ->
				val entity = response.entity

				val stringResponse = EntityUtils.toString(entity)

				gson.fromJson(stringResponse, ClientInfo::class.java)
			}
		}
	}
}