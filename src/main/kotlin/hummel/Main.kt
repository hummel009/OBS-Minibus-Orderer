package hummel

import com.google.gson.Gson
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpOptions
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.FileReader
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.timerTask

fun main() {
	val gson = Gson()
	val config = try {
		val configJson = FileReader("config.json")
		gson.fromJson(configJson, Config::class.java)
	} catch (e: Exception) {
		println("Config not found! Default config loaded.")
		defaultConfig
	}

	println("Choose mode: timer or infinite")
	val scan = Scanner(System.`in`)
	val answer = scan.nextLine()

	if (answer == "timer") {
		val currentTime = LocalTime.now()
		val hour = config.orderingTime.first
		val minute = config.orderingTime.second
		val second = config.orderingTime.third

		val timer = Timer()
		val task = timerTask {
			if (currentTime.hour == hour && currentTime.minute == minute && currentTime.second == second) {
				orderShuttle(config)
				timer.cancel()
			}
		}
		val timeUntil = getTimeUntil(minute, minute, second)
		timer.schedule(task, timeUntil)
	} else {
		orderShuttle(config)
	}
}

fun orderShuttle(config: Config) {
	while (true) {
		unlockUserInfo(config)

		val userInfo = getUserInfo(config)
		val shouldExecute = getIsTicketNotOrdered(userInfo, config)

		if (shouldExecute) {
			val bookingsInfo = getBookingsInfo()
			val bookingIDs = getBookingIDs(bookingsInfo, config)

			println("From City ID: " + bookingIDs.first)
			println("To City ID: " + bookingIDs.second)

			val transfersInfo = getTransfersInfo(bookingIDs.first, bookingIDs.second, config)
			val transferIDs = getTransferIDs(transfersInfo, config)

			println("From Stop ID: ${transferIDs.first}")
			println("To Stop ID: ${transferIDs.second}")
			println("Time ID: ${transferIDs.third}")

			orderTicket(transferIDs.first, transferIDs.second, transferIDs.third, config)

			println("Retry in 60 seconds!")
			Thread.sleep(60000)
		} else {
			println("The shuttle is already ordered!")
			break
		}
	}
}

fun unlockUserInfo(config: Config) {
	val client = HttpClients.createDefault()
	val request = HttpOptions("https://api.obs.by/clients/withReservations/${config.phone}")

	request.addHeader("Access-Control-Request-Headers", "authorization")
	request.addHeader("Access-Control-Request-Method", "GET")

	val response = client.execute(request)

	response.close()
	client.close()
}

fun getUserInfo(config: Config): String {
	val client = HttpClients.createDefault()
	val request = HttpGet("https://api.obs.by/clients/withReservations/${config.phone}")

	request.addHeader("Authorization", "Bearer ${config.token}")

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

fun getTransfersInfo(fromCityID: String, toCityID: String, config: Config): String {
	val client = HttpClients.createDefault()
	val request = HttpPost("https://api.obs.by/transfers/betweenCities")

	val payload = payloadTransfersInfo(fromCityID, toCityID, config)
	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	val response = client.execute(request)
	val entity = response.entity
	val transfersInfo = EntityUtils.toString(entity)

	response.close()
	client.close()

	return transfersInfo
}

fun orderTicket(fromStopID: String, toStopID: String, timeID: String, config: Config) {
	val client = HttpClients.createDefault()
	val request = HttpPost("https://api.obs.by/reservations/book")
	request.addHeader("Authorization", "Bearer ${config.token}")

	val payload = payloadOrderTicket(fromStopID, toStopID, timeID, config)
	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	val response = client.execute(request)

	response.close()
	client.close()
}