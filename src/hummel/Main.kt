package hummel

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpOptions
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.timerTask

fun main() {
	val timer = Timer()
	val task = timerTask {
		val currentTime = LocalTime.now()
		if (currentTime.hour == orderingTime.first && currentTime.minute == orderingTime.second && currentTime.second == orderingTime.third) {
			orderShuttle()
			timer.cancel()
		}
	}
	val timeUntil = getTimeUntil(orderingTime.first, orderingTime.second, orderingTime.third)
	timer.schedule(task, timeUntil)
}

fun orderShuttle() {
	while (true) {
		unlockUserInfo()

		val userInfo = getUserInfo()
		val shouldExecute = getIsTicketNotOrdered(userInfo)

		if (shouldExecute) {
			val bookingsInfo = getBookingsInfo()
			val bookingIDs = getBookingIDs(bookingsInfo)

			println("From City ID: " + bookingIDs.first)
			println("To City ID: " + bookingIDs.second)

			val transfersInfo = getTransfersInfo(bookingIDs.first, bookingIDs.second, date)
			val transferIDs = getTransferIDs(transfersInfo, time)

			println("From Stop ID: ${transferIDs.first}")
			println("To Stop ID: ${transferIDs.second}")
			println("Time ID: ${transferIDs.third}")

			orderTicket(transferIDs.first, transferIDs.second, transferIDs.third)

			println("Retry in 60 seconds!")
			Thread.sleep(60000)
		} else {
			println("The shuttle is already ordered!")
			break
		}
	}
}

fun unlockUserInfo() {
	val client = HttpClients.createDefault()
	val request = HttpOptions("https://api.obs.by/clients/withReservations/$phone")

	request.addHeader("Access-Control-Request-Headers", "authorization")
	request.addHeader("Access-Control-Request-Method", "GET")

	val response = client.execute(request)

	response.close()
	client.close()
}

fun getUserInfo(): String {
	val client = HttpClients.createDefault()
	val request = HttpGet("https://api.obs.by/clients/withReservations/$phone")

	request.addHeader("Authorization", "Bearer $token")

	val response = client.execute(request)
	val entity = response.entity
	val userInfo = EntityUtils.toString(entity)

	response.close()
	client.close()

	return userInfo
}

fun getBookingsInfo(): String {
	val client = HttpClients.createDefault()
	val request = HttpGet("https://api.obs.by/cities/forBooking")

	val response = client.execute(request)
	val entity = response.entity
	val bookingsInfo = EntityUtils.toString(entity)

	response.close()
	client.close()

	return bookingsInfo
}

fun getTransfersInfo(fromCityID: String, toCityID: String, date: String): String {
	val client = HttpClients.createDefault()
	val request = HttpPost("https://api.obs.by/transfers/betweenCities")

	val payload = payloadTransfersInfo(fromCityID, toCityID, date)
	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	val response = client.execute(request)
	val entity = response.entity
	val transfersInfo = EntityUtils.toString(entity)

	response.close()
	client.close()

	return transfersInfo
}

fun orderTicket(fromStopID: String, toStopID: String, timeID: String) {
	val client = HttpClients.createDefault()
	val request = HttpPost("https://api.obs.by/reservations/book")
	request.addHeader("Authorization", "Bearer $token")

	val payload = payloadOrderTicket(fromStopID, toStopID, timeID)
	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	val response = client.execute(request)

	response.close()
	client.close()
}