package com.github.hummel.obsby.dao

import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.StringEntity

object ReservationsDao {
	fun postBook(
		phone: String, token: String, transferId: String, stopFromId: String, stopToId: String
	) {
		HttpClients.createDefault().use {
			val request = HttpPost("https://api.obs.by/reservations/book")
			val payload = """
			{
				"client": "$phone",
				"transfer": "$transferId",
				"from": "$stopFromId",
				"to": "$stopToId",
				"amount": 1,
				"info": "",
				"createdBy": {
					"role": "web-client",
					"phone": "$phone"
				}
			}
			""".trimIndent()

			request.addHeader("Authorization", "Bearer $token")
			request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

			it.execute(request) {}
		}
	}
}