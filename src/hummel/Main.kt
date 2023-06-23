package hummel

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.timerTask

const val phone: String = "+375296186182"

fun main() {
	val timer = Timer()
	val task = timerTask {
		val currentTime = LocalTime.now()
		if (currentTime.hour == 3 && currentTime.minute == 1 && currentTime.second == 0) {
			orderTaxi()
			timer.cancel()
		}
	}
	val delay = calculateDelayUntil(3, 1, 0)
	timer.schedule(task, delay)
}

fun orderTaxi() {
	val responseForBooking = requestForBooking()
	val idsForBooking = getIDsForBooking(responseForBooking)

	println("From City ID: " + idsForBooking.first)
	println("To City ID: " + idsForBooking.second)

	val responseBetweenCities = requestBetweenCities(idsForBooking.first, idsForBooking.second, "2023-06-25")
	val idsBetweenCities = getIDsBetweenCities(responseBetweenCities, "07:45")

	println("From Stop ID: ${idsBetweenCities.first}")
	println("To Stop ID: ${idsBetweenCities.second}")
	println("Time ID: ${idsBetweenCities.third}")

	requestBook(idsBetweenCities.first, idsBetweenCities.second, idsBetweenCities.third)
}

fun requestForBooking(): String {
	val httpClient = HttpClients.createDefault()
	val httpGet = HttpGet("https://api.obs.by/cities/forBooking")

	val response = httpClient.execute(httpGet)
	val entity = response.entity
	val responseForBooking = EntityUtils.toString(entity)

	httpClient.close()

	return responseForBooking
}

fun requestBetweenCities(fromCityID: String, toCityID: String, date: String): String {
	val httpClient = HttpClients.createDefault()
	val httpPost = HttpPost("https://api.obs.by/transfers/betweenCities")

	val payload = payloadBetweenCities(fromCityID, toCityID, date)
	httpPost.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	val response = httpClient.execute(httpPost)
	val entity = response.entity
	val responseBetweenCities = EntityUtils.toString(entity)

	httpClient.close()

	return responseBetweenCities
}

fun requestBook(fromStopID: String, toStopID: String, timeID: String) {
	val httpClient = HttpClients.createDefault()
	val httpPost = HttpPost("https://api.obs.by/reservations/book")

	httpPost.addHeader("Accept", "application/json, text/plain, */*")
	httpPost.addHeader("Accept-Encoding", "gzip, deflate, br")
	httpPost.addHeader("Accept-Language", "ru-RU,ru;q=0.9,be-BY;q=0.8,be;q=0.7,en-US;q=0.6,en;q=0.5")
	httpPost.addHeader(
		"Authorization",
		"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZSI6IiszNzUyOTYxODYxODIiLCJyb2xlIjoiY2xpZW50IiwiaWF0IjoxNjg3MzU0NTQwLCJleHAiOjE2ODc0NDA5NDB9.v3wQlCAPDnQ6XeVFSz0Ez8px4nOstUM3sR10cm_oivw"
	)

	val payload = payloadBook(fromStopID, toStopID, timeID)
	httpPost.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	val response = httpClient.execute(httpPost)

	response.close()
	httpClient.close()
}