package hummel

import com.google.gson.Gson
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpOptions
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.GridLayout
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.LocalTime
import java.util.Timer
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.timerTask

fun main() {
	EventQueue.invokeLater {
		try {
			for (info in UIManager.getInstalledLookAndFeels()) {
				if ("Windows Classic" == info.name) {
					UIManager.setLookAndFeel(info.className)
					break
				}
			}
			val frame = GUI()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI : JFrame() {
	private var timer = true

	init {
		title = "Hummel009's Shuttle Bot"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(100, 100, 500, 150)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = BorderLayout(0, 0)
		contentPanel.layout = GridLayout(0, 1, 0, 0)
		contentPane = contentPanel

		val radioPanel = JPanel()
		val radioTimer = JRadioButton("Таймер (заказ ночью)")
		val radioMonitoring = JRadioButton("Мониторинг (попытки раз в минуту)")
		radioTimer.isSelected = true
		radioTimer.addActionListener {
			timer = true
			radioMonitoring.isSelected = false
		}
		radioMonitoring.addActionListener {
			timer = false
			radioTimer.isSelected = false
		}
		radioPanel.add(radioTimer)
		radioPanel.add(radioMonitoring)

		val processPanel = JPanel()
		val processButton = JButton("Запуск")
		processButton.addActionListener { process() }
		processPanel.add(processButton)

		contentPanel.add(radioPanel)
		contentPanel.add(processPanel)

		setLocationRelativeTo(null)
	}

	private fun process() {
		val gson = Gson()
		val config = try {
			val file = File("config.json")
			val configJson = BufferedReader(InputStreamReader(file.inputStream(), StandardCharsets.UTF_8))
			gson.fromJson(configJson, Config::class.java)
		} catch (e: Exception) {
			JOptionPane.showMessageDialog(this, "Настройки не заданы!", "Error", JOptionPane.ERROR_MESSAGE)
			return
		}

		println("Debug:")
		println("Ordering Time: ${config.orderingTime}")
		println("Phone: ${config.phone}")
		println("Date: ${config.date}")
		println("Time: ${config.time}")
		println("Stop From: ${config.stopFrom}")
		println("Stop To: ${config.stopTo}")
		println("City From: ${config.cityFrom}")
		println("City To: ${config.cityTo}")
		println("Token: ${config.token}")

		if (timer) {
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

	private fun orderShuttle(config: Config) {
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
				JOptionPane.showMessageDialog(this, "Билет заказан.", "Message", JOptionPane.INFORMATION_MESSAGE)
				break
			}
		}
	}

	private fun unlockUserInfo(config: Config) {
		val client = HttpClients.createDefault()
		val request = HttpOptions("https://api.obs.by/clients/withReservations/${config.phone}")

		request.addHeader("Access-Control-Request-Headers", "authorization")
		request.addHeader("Access-Control-Request-Method", "GET")

		val response = client.execute(request)

		response.close()
		client.close()
	}

	private fun getUserInfo(config: Config): String {
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

	private fun getBookingsInfo(): String {
		val client = HttpClients.createDefault()
		val request = HttpGet("https://api.obs.by/cities/forBooking")

		val response = client.execute(request)
		val entity = response.entity
		val bookingsInfo = EntityUtils.toString(entity)

		response.close()
		client.close()

		return bookingsInfo
	}

	private fun getTransfersInfo(fromCityID: String, toCityID: String, config: Config): String {
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

	private fun orderTicket(fromStopID: String, toStopID: String, timeID: String, config: Config) {
		val client = HttpClients.createDefault()
		val request = HttpPost("https://api.obs.by/reservations/book")
		request.addHeader("Authorization", "Bearer ${config.token}")

		val payload = payloadOrderTicket(fromStopID, toStopID, timeID, config)
		request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

		val response = client.execute(request)

		response.close()
		client.close()
	}
}