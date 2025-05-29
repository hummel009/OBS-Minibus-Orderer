package com.github.hummel.obsmo

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme
import com.github.hummel.obsmo.service.CitiesService
import com.github.hummel.obsmo.service.ClientsService
import com.github.hummel.obsmo.service.ReservationsService
import com.github.hummel.obsmo.service.TransfersService
import java.awt.EventQueue
import java.awt.GridLayout
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
	FlatLightLaf.setup()
	EventQueue.invokeLater {
		try {
			UIManager.setLookAndFeel(FlatMTGitHubDarkIJTheme())
			val frame = GUI()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI : JFrame() {
	private var cache: Cache = Cache()

	private var citiesFromNames: Array<String> = arrayOf("Не выбрано...")
	private var citiesToNames: Array<String> = arrayOf("Не выбрано...")
	private var stopsFromNames: Array<String> = arrayOf("Не выбрано...")
	private var stopsToNames: Array<String> = arrayOf("Не выбрано...")
	private var times: Array<String> = arrayOf("Не выбрано...")

	private val citiesFromNamesDropdown: JComboBox<String?> = JComboBox(citiesFromNames)
	private val citiesToNamesDropdown: JComboBox<String?> = JComboBox(citiesToNames)
	private val stopsFromNamesDropdown: JComboBox<String?> = JComboBox(stopsFromNames)
	private val stopsToNamesDropdown: JComboBox<String?> = JComboBox(stopsToNames)
	private val timesDropdown: JComboBox<String?> = JComboBox(times)

	private val phoneField: JTextField = JTextField(20)
	private val tokenField: JTextField = JTextField(20)
	private val dateField: JTextField = JTextField(20)

	private val refreshCitiesFromButton: JButton = JButton("Обновить список городов отправки")
	private val refreshCitiesToButton: JButton = JButton("Обновить список городов прибытия")
	private val refreshTimesFromButton: JButton = JButton("Обновить доступные времена отправки")
	private val refreshStopsFromButton: JButton = JButton("Обновить список остановок отправки")
	private val refreshStopsToButton: JButton = JButton("Обновить список остановок прибытия")

	private val startButton: JButton = JButton("Запуск бота")

	private val shutdownCheckbox: JCheckBox = JCheckBox("Гибернация ПК")
	private val exitCheckbox: JCheckBox = JCheckBox("Выключение бота")

	init {
		title = "Hummel009's Shuttle Bot"
		defaultCloseOperation = EXIT_ON_CLOSE

		setBounds(0, 0, 400, 600)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = GridLayout(0, 1, 0, 0)
		contentPane = contentPanel

		val checkboxPanel = createCheckboxPanel()
		val tokenPanel = createTokenPanel()
		val phonePanel = createPhonePanel()
		val refreshCitiesFromButton = createRefreshCitiesFromButton()
		val citiesFromPanel = createCitiesFromPanel()
		val refreshCitiesToButton = createRefreshCitiesToButton()
		val citiesToPanel = createCitiesToPanel()
		val datePanel = createDatePanel()
		val refreshTimesButton = createRefreshTimesFromButton()
		val timePanel = createTimePanel()
		val refreshStopsFromButton = createRefreshStopsFromButton()
		val stopsFromPanel = createStopsFromPanel()
		val refreshStopsToButton = createRefreshStopsToButton()
		val stopsToPanel = createStopsToPanel()
		val startButton = createStartButton()

		contentPanel.add(checkboxPanel)
		contentPanel.add(tokenPanel)
		contentPanel.add(phonePanel)
		contentPanel.add(datePanel)
		contentPanel.add(refreshCitiesFromButton)
		contentPanel.add(citiesFromPanel)
		contentPanel.add(refreshCitiesToButton)
		contentPanel.add(citiesToPanel)
		contentPanel.add(refreshTimesButton)
		contentPanel.add(timePanel)
		contentPanel.add(refreshStopsFromButton)
		contentPanel.add(stopsFromPanel)
		contentPanel.add(refreshStopsToButton)
		contentPanel.add(stopsToPanel)
		contentPanel.add(startButton)

		setLocationRelativeTo(null)
	}

	private fun createStartButton(): JButton {
		startButton.isEnabled = false
		startButton.addActionListener {
			startButton.isEnabled = false

			stopsToNamesDropdown.isEnabled = false
			startButton.isEnabled = false

			shutdownCheckbox.isEnabled = false
			exitCheckbox.isEnabled = false

			val pause = 60

			thread {
				loop@ while (true) {
					val currentTime = LocalTime.now(ZoneId.systemDefault())
					val time = "%02d:%02d".format(currentTime.hour, currentTime.minute)

					val notOrdered = ClientsService.isTicketNotOrdered(
						cache = cache,
						phone = phoneField.text,
						token = tokenField.text,
						date = dateField.text,
						cityFromName = citiesFromNamesDropdown.getSelectedItemString(),
						cityToName = citiesToNamesDropdown.getSelectedItemString(),
						time = timesDropdown.getSelectedItemString(),
						stopFromName = stopsFromNamesDropdown.getSelectedItemString()
					)

					if (cache.transfersInfoPseudo) {
						println("[$time] Расписание не открыто. Следующая попытка через $pause секунд.")

						Thread.sleep(pause * 1000L)

						continue@loop
					}

					if (notOrdered) {
						ReservationsService.postBook(
							cache = cache,
							phone = phoneField.text,
							token = tokenField.text,
							time = timesDropdown.getSelectedItemString(),
							stopFromName = stopsFromNamesDropdown.getSelectedItemString(),
							stopToName = stopsToNamesDropdown.getSelectedItemString()
						)

						println("[$time] Попытка завершена. Следующая попытка через $pause секунд.")

						Thread.sleep(pause * 1000L)

						continue@loop
					}

					println("[$time] Билет заказан.")

					if (!exitCheckbox.isSelected && !shutdownCheckbox.isSelected) {
						JOptionPane.showMessageDialog(
							this, "[$time] Билет заказан.", "Message", JOptionPane.INFORMATION_MESSAGE
						)
					}
					break@loop
				}
			}

			if (exitCheckbox.isSelected) {
				exitProcess(0)
			}

			if (shutdownCheckbox.isSelected) {
				try {
					val processBuilder = ProcessBuilder("rundll32.exe", "powrprof.dll,SetSuspendState", "0,1,0")
					val process = processBuilder.start()
					val exitCode = process.waitFor()
					if (exitCode == 0) {
						println("Command executed successfully.")
					} else {
						println("Command execution failed with exit code: $exitCode")
					}
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}

		return startButton
	}

	private fun createStopsToPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Остановка прибытия:")

		stopsToNamesDropdown.isEnabled = false
		stopsToNamesDropdown.selectedItem = stopsToNames[0]

		panel.add(left)
		panel.add(stopsToNamesDropdown)

		return panel
	}

	private fun createRefreshStopsToButton(): JButton {
		refreshStopsToButton.isEnabled = false
		refreshStopsToButton.addActionListener {
			stopsToNames = TransfersService.getStopsToNames(
				cache = cache,
				time = timesDropdown.getSelectedItemString(),
				stopFromName = stopsFromNamesDropdown.getSelectedItemString()
			)

			stopsToNamesDropdown.removeAllItems()
			stopsToNames.forEach { stopsToNamesDropdown.addItem(it) }

			stopsToNamesDropdown.selectedItem = stopsToNames[0]
			stopsToNamesDropdown.isEnabled = true

			refreshStopsToButton.isEnabled = false
			startButton.isEnabled = true

			stopsFromNamesDropdown.isEnabled = false
			refreshStopsFromButton.isEnabled = false
		}

		return refreshStopsToButton
	}

	private fun createStopsFromPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Остановка отправки:")

		stopsFromNamesDropdown.isEnabled = false
		stopsFromNamesDropdown.selectedItem = stopsFromNames[0]

		panel.add(left)
		panel.add(stopsFromNamesDropdown)

		return panel
	}

	private fun createRefreshStopsFromButton(): JButton {
		refreshStopsFromButton.isEnabled = false
		refreshStopsFromButton.addActionListener {
			stopsFromNames = TransfersService.getStopsFromNames(
				cache = cache, time = timesDropdown.getSelectedItemString()
			)

			stopsFromNamesDropdown.removeAllItems()
			stopsFromNames.forEach { stopsFromNamesDropdown.addItem(it) }

			stopsFromNamesDropdown.selectedItem = stopsFromNames[0]
			stopsFromNamesDropdown.isEnabled = true

			refreshStopsFromButton.isEnabled = false
			refreshStopsToButton.isEnabled = true

			timesDropdown.isEnabled = false
			refreshTimesFromButton.isEnabled = false
		}

		return refreshStopsFromButton
	}

	private fun createTimePanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Время отправки:")

		timesDropdown.isEnabled = false
		timesDropdown.selectedItem = times[0]

		panel.add(left)
		panel.add(timesDropdown)

		return panel
	}

	private fun createRefreshTimesFromButton(): JButton {
		refreshTimesFromButton.isEnabled = false
		refreshTimesFromButton.addActionListener {
			times = TransfersService.getTimes(
				cache = cache,
				phone = phoneField.text,
				date = dateField.text,
				cityFromName = citiesFromNamesDropdown.getSelectedItemString(),
				cityToName = citiesToNamesDropdown.getSelectedItemString()
			)

			timesDropdown.removeAllItems()
			times.forEach { timesDropdown.addItem(it) }

			timesDropdown.selectedItem = times[0]
			timesDropdown.isEnabled = true

			refreshTimesFromButton.isEnabled = false
			refreshStopsFromButton.isEnabled = true

			citiesToNamesDropdown.isEnabled = false
		}

		return refreshTimesFromButton
	}

	private fun createCitiesToPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Город прибытия:")

		citiesToNamesDropdown.isEnabled = false
		citiesToNamesDropdown.selectedItem = citiesToNames[0]

		panel.add(left)
		panel.add(citiesToNamesDropdown)

		return panel
	}

	private fun createRefreshCitiesToButton(): JButton {
		refreshCitiesToButton.isEnabled = false
		refreshCitiesToButton.addActionListener {
			citiesToNames = CitiesService.getCitiesToNames(
				cache = cache, cityFromName = citiesFromNamesDropdown.getSelectedItemString()
			)

			citiesToNamesDropdown.removeAllItems()
			citiesToNames.forEach { citiesToNamesDropdown.addItem(it) }

			citiesToNamesDropdown.selectedItem = citiesToNames[0]
			citiesToNamesDropdown.isEnabled = true

			refreshCitiesToButton.isEnabled = false
			refreshTimesFromButton.isEnabled = true

			citiesFromNamesDropdown.isEnabled = false
		}

		return refreshCitiesToButton
	}

	private fun createCitiesFromPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Город отправки:")

		citiesFromNamesDropdown.isEnabled = false
		citiesFromNamesDropdown.selectedItem = citiesFromNames[0]

		panel.add(left)
		panel.add(citiesFromNamesDropdown)

		return panel
	}

	private fun createRefreshCitiesFromButton(): JButton {
		refreshCitiesFromButton.addActionListener {
			citiesFromNames = CitiesService.getCitiesFromNames(cache)

			citiesFromNamesDropdown.removeAllItems()
			citiesFromNames.forEach { citiesFromNamesDropdown.addItem(it) }

			citiesFromNamesDropdown.selectedItem = citiesFromNames[0]
			citiesFromNamesDropdown.isEnabled = true

			refreshCitiesFromButton.isEnabled = false
			refreshCitiesToButton.isEnabled = true

			phoneField.isEnabled = false
			tokenField.isEnabled = false
			dateField.isEnabled = false
		}

		return refreshCitiesFromButton
	}

	private fun createDatePanel(): JPanel {
		val panel = JPanel()
		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Дата отправки:")

		dateField.text = LocalDate.now().format(formatter)

		panel.add(left)
		panel.add(dateField)

		return panel
	}

	private fun createTokenPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Токен:")

		tokenField.text =
			"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZSI6IiszNzUyOTYxODYxODMiLCJyb2xlIjoiY2xpZW50IiwiaWF0IjoxNzI4OTIzMDM0fQ.qfks2dNmBB2XBPkAQMJBDNtePgA_Ci3K2wl5B5MMvYU"

		panel.add(left)
		panel.add(tokenField)

		return panel
	}

	private fun createPhonePanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Номер телефона:")

		phoneField.text = "+375296186183"

		panel.add(left)
		panel.add(phoneField)

		return panel
	}

	private fun createCheckboxPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		shutdownCheckbox.isSelected = false
		exitCheckbox.isSelected = false

		panel.add(shutdownCheckbox)
		panel.add(exitCheckbox)

		return panel
	}
}
