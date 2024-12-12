package com.github.hummel.shuttle

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import com.github.hummel.shuttle.service.CitiesService
import com.github.hummel.shuttle.service.ClientsService
import com.github.hummel.shuttle.service.ReservationsService
import com.github.hummel.shuttle.service.TransfersService
import java.awt.EventQueue
import java.awt.GridLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.thread
import kotlin.system.exitProcess

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
	private var cache = Cache()

	private var citiesFromNames = arrayOf("Не выбрано...")
	private var citiesToNames = arrayOf("Не выбрано...")
	private var stopsFromNames = arrayOf("Не выбрано...")
	private var stopsToNames = arrayOf("Не выбрано...")
	private var times = arrayOf("Не выбрано...")

	private val citiesFromNamesDropdown = JComboBox(citiesFromNames)
	private val citiesToNamesDropdown = JComboBox(citiesToNames)
	private val stopsFromNamesDropdown = JComboBox(stopsFromNames)
	private val stopsToNamesDropdown = JComboBox(stopsToNames)
	private val timesDropdown = JComboBox(times)

	private val phoneField = JTextField(20)
	private val tokenField = JTextField(20)
	private val dateField = JTextField(20)

	private val refreshCitiesFromButton = JButton("Обновить список городов отправки")
	private val refreshCitiesToButton = JButton("Обновить список городов прибытия")
	private val refreshTimesFromButton = JButton("Обновить доступные времена отправки")
	private val refreshStopsFromButton = JButton("Обновить список остановок отправки")
	private val refreshStopsToButton = JButton("Обновить список остановок прибытия")

	private val startButton = JButton("Запуск бота")

	private val shutdownCheckbox = JCheckBox("Гибернация ПК")
	private val exitCheckbox = JCheckBox("Выключение бота")

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

			thread {
				loop@ while (true) {
					val currentTime = System.currentTimeMillis()
					val minutes = (currentTime / (1000 * 60)) % 60
					val hours = (currentTime / (1000 * 60 * 60)) % 24
					val time = "%02d:%02d".format(hours, minutes)

					ClientsService.unlock(phoneField.text)

					val shouldExecute = ClientsService.isTicketNotOrdered(
						cache,
						phoneField.text,
						tokenField.text,
						dateField.text,
						timesDropdown.getSelectedItemString(),
						stopsFromNamesDropdown.getSelectedItemString()
					)

					if (shouldExecute) {
						ReservationsService.postBook(
							cache,
							phoneField.text,
							tokenField.text,
							dateField.text,
							citiesFromNamesDropdown.getSelectedItemString(),
							citiesToNamesDropdown.getSelectedItemString(),
							timesDropdown.getSelectedItemString(),
							stopsFromNamesDropdown.getSelectedItemString(),
							stopsToNamesDropdown.getSelectedItemString()
						)

						println("[$time] Попытка завершена. Следующая попытка через 60 секунд.")

						Thread.sleep(60000)
					} else {
						println("[$time] Билет заказан.")

						if (!exitCheckbox.isSelected && !shutdownCheckbox.isSelected) {
							JOptionPane.showMessageDialog(
								this, "[$time] Билет заказан.", "Message", JOptionPane.INFORMATION_MESSAGE
							)
						}
						break@loop
					}
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
			ClientsService.unlock(phoneField.text)

			stopsToNames = TransfersService.getStopsToNames(
				cache, timesDropdown.getSelectedItemString(), stopsFromNamesDropdown.getSelectedItemString()
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
			ClientsService.unlock(phoneField.text)

			stopsFromNames = TransfersService.getStopsFromNames(
				cache, timesDropdown.getSelectedItemString()
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
			ClientsService.unlock(phoneField.text)

			times = TransfersService.getTimes(
				cache,
				phoneField.text,
				dateField.text,
				citiesFromNamesDropdown.getSelectedItemString(),
				citiesToNamesDropdown.getSelectedItemString()
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
			ClientsService.unlock(phoneField.text)

			citiesToNames = CitiesService.getCitiesToNames(
				cache, citiesFromNamesDropdown.getSelectedItemString()
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
			ClientsService.unlock(phoneField.text)

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
