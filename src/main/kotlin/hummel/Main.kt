package hummel

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpOptions
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.awt.AWTException
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.GridLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.thread
import kotlin.concurrent.timerTask
import kotlin.system.exitProcess


fun main() {
	FlatLightLaf.setup()
	EventQueue.invokeLater {
		try {
			UIManager.setLookAndFeel(FlatGitHubDarkIJTheme())
			val frame = GUI()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI : JFrame() {
	init {
		title = "Hummel009's Shuttle Bot"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(0, 0, 600, 500)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = BorderLayout(0, 0)
		contentPane = contentPanel

		var timer = true

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

		val inputPanel = JPanel()
		inputPanel.layout = GridLayout(0, 2, 5, 5)

		val checkbox1 = JCheckBox("Гибернация ПК")
		checkbox1.isSelected = false
		inputPanel.add(checkbox1)
		val checkbox2 = JCheckBox("Выключение бота")
		checkbox2.isSelected = true
		inputPanel.add(checkbox2)

		inputPanel.add(JLabel("Номер телефона:"))
		val phoneField = JTextField(20)
		phoneField.text = "+375296186182"
		inputPanel.add(phoneField)

		inputPanel.add(JLabel("Дата отправки:"))
		val dateField = JTextField(20)
		val currentDate = LocalDate.now()
		val futureDate = currentDate.plusDays(8)
		val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
		val formattedDate = futureDate.format(formatter)
		dateField.text = formattedDate
		inputPanel.add(dateField)

		inputPanel.add(JLabel("Время отправки:"))
		val timeField = JTextField(20)
		timeField.text = "07:45"
		inputPanel.add(timeField)

		inputPanel.add(JLabel("Остановка отправки:"))
		val stopFromField = JTextField(20)
		stopFromField.text = "РДК"
		inputPanel.add(stopFromField)

		inputPanel.add(JLabel("Остановка прибытия:"))
		val stopToField = JTextField(20)
		stopToField.text = "ст.м.Восток"
		inputPanel.add(stopToField)

		inputPanel.add(JLabel("Город отправки:"))
		val cityFromField = JTextField(20)
		cityFromField.text = "Логойск"
		inputPanel.add(cityFromField)

		inputPanel.add(JLabel("Город прибытия:"))
		val cityToField = JTextField(20)
		cityToField.text = "Минск"
		inputPanel.add(cityToField)

		inputPanel.add(JLabel("Токен:"))
		val tokenField = JTextField(20)
		tokenField.text =
			"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZSI6IiszNzUyOTYxODYxODIiLCJyb2xlIjoiY2xpZW50IiwiaWF0IjoxNjg3MzU0NTQwLCJleHAiOjE2ODc0NDA5NDB9.v3wQlCAPDnQ6XeVFSz0Ez8px4nOstUM3sR10cm_oivw"
		inputPanel.add(tokenField)

		var data: Data
		val saveButton = JButton("Запуск")
		saveButton.addActionListener {
			thread {
				data = Data(
					phoneField.text,
					dateField.text,
					timeField.text,
					stopFromField.text,
					stopToField.text,
					cityFromField.text,
					cityToField.text,
					tokenField.text,
					timer,
					checkbox1.isSelected,
					checkbox2.isSelected
				)
				process(data)
			}
		}

		contentPanel.add(radioPanel, BorderLayout.NORTH)
		contentPanel.add(inputPanel, BorderLayout.CENTER)
		contentPanel.add(saveButton, BorderLayout.SOUTH)

		setLocationRelativeTo(null)
	}

	private fun process(data: Data) {
		println("Debug:")
		println("Phone: ${data.phone}")
		println("Date: ${data.date}")
		println("Time: ${data.time}")
		println("Stop From: ${data.stopFrom}")
		println("Stop To: ${data.stopTo}")
		println("City From: ${data.cityFrom}")
		println("City To: ${data.cityTo}")
		println("Token: ${data.token}")
		println("Timer: ${data.timer}")
		println("Shutdown: ${data.shutdown}")
		println("Exit: ${data.exit}")
		if (data.timer) {
			val timer = Timer()
			val currentTime = System.currentTimeMillis()
			val targetTime = calculateTargetTime(0, 1, 0)

			val timeUntil = targetTime - currentTime
			if (timeUntil > 0) {
				val hoursRemaining = timeUntil / 3600000
				val minutesRemaining = (timeUntil % 3600000) / 60000
				val secondsRemaining = ((timeUntil % 3600000) % 60000) / 1000
				println("Time until timer starts: $hoursRemaining hours, $minutesRemaining minutes, $secondsRemaining seconds")

				val task = timerTask {
					orderShuttle(data)
					timer.cancel()
				}
				timer.schedule(task, timeUntil)
			} else {
				println("Target time has already passed.")
			}
		} else {
			orderShuttle(data)
		}
	}

	private fun orderShuttle(data: Data) {
		while (true) {
			try {
				unlockUserInfo(data)

				val userInfo = getUserInfo(data)
				val shouldExecute = getIsTicketNotOrdered(userInfo, data)

				if (shouldExecute) {
					val currentTime = getCurrentTime()
					val bookingsInfo = getBookingsInfo()
					val bookingIDs = getBookingIDs(bookingsInfo, data)

					println("[$currentTime] From City ID: " + bookingIDs.first)
					println("[$currentTime] To City ID: " + bookingIDs.second)

					val transfersInfo = getTransfersInfo(bookingIDs.first, bookingIDs.second, data)
					val transferIDs = getTransferIDs(transfersInfo, data)

					println("[$currentTime] From Stop ID: ${transferIDs.first}")
					println("[$currentTime] To Stop ID: ${transferIDs.second}")
					println("[$currentTime] Time ID: ${transferIDs.third}")

					orderTicket(transferIDs.first, transferIDs.second, transferIDs.third, data)

					println("Retry in 60 seconds!")
					Thread.sleep(60000)
				} else {
					if (data.exit) {
						exitProcess(0)
					}
					if (!data.shutdown) {
						JOptionPane.showMessageDialog(
							this, "Билет заказан.", "Message", JOptionPane.INFORMATION_MESSAGE
						)
					}
					break
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
		if (data.shutdown) {
			try {
				val runtime = Runtime.getRuntime()
				runtime.exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0")
			} catch (e: AWTException) {
				e.printStackTrace()
			}
		}
	}

	private fun unlockUserInfo(data: Data) {
		val client = HttpClients.createDefault()
		val request = HttpOptions("https://api.obs.by/clients/withReservations/${data.phone}")

		request.addHeader("Access-Control-Request-Headers", "authorization")
		request.addHeader("Access-Control-Request-Method", "GET")

		val response = client.execute(request)

		response.close()
		client.close()
	}

	private fun getUserInfo(data: Data): String {
		val client = HttpClients.createDefault()
		val request = HttpGet("https://api.obs.by/clients/withReservations/${data.phone}")

		request.addHeader("Authorization", "Bearer ${data.token}")

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

	private fun getTransfersInfo(fromCityID: String, toCityID: String, data: Data): String {
		val client = HttpClients.createDefault()
		val request = HttpPost("https://api.obs.by/transfers/betweenCities")

		val payload = payloadTransfersInfo(fromCityID, toCityID, data)
		request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

		val response = client.execute(request)
		val entity = response.entity
		val transfersInfo = EntityUtils.toString(entity)

		response.close()
		client.close()

		return transfersInfo
	}

	private fun orderTicket(fromStopID: String, toStopID: String, timeID: String, data: Data) {
		val client = HttpClients.createDefault()
		val request = HttpPost("https://api.obs.by/reservations/book")
		request.addHeader("Authorization", "Bearer ${data.token}")

		val payload = payloadOrderTicket(fromStopID, toStopID, timeID, data)
		request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

		val response = client.execute(request)

		response.close()
		client.close()
	}
}