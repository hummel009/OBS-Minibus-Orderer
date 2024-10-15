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

val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
	var cache = Cache()

	var citiesFromNames = arrayOf("Не выбрано...")
	var citiesToNames = arrayOf("Не выбрано...")
	var stopsFromNames = arrayOf("Не выбрано...")
	var stopsToNames = arrayOf("Не выбрано...")
	var times = arrayOf("Не выбрано...")

	val citiesFromNamesDropdown = JComboBox(citiesFromNames)
	val citiesToNamesDropdown = JComboBox(citiesToNames)
	val stopsFromNamesDropdown = JComboBox(stopsFromNames)
	val stopsToNamesDropdown = JComboBox(stopsToNames)
	val timesDropdown = JComboBox(times)

	val phoneField = JTextField(20)
	val tokenField = JTextField(20)
	val dateField = JTextField(20)

	val refreshCitiesFromButton = JButton("Обновить список городов")
	val refreshCitiesToButton = JButton("Обновить список городов прибытия")
	val refreshTimesFromButton = JButton("Обновить доступные времена отправки")
	val refreshStopsFromButton = JButton("Обновить список остановок отправки")
	val refreshStopsToButton = JButton("Обновить список остановок прибытия")

	val startButton = JButton("Запуск бота")

	val timeRadioButton = JRadioButton("Таймер (заказ ночью)")
	val loopRadioButton = JRadioButton("Мониторинг (попытки раз в минуту)")

	val shutdownCheckbox = JCheckBox("Гибернация ПК")
	val exitCheckbox = JCheckBox("Выключение бота")

	init {
		title = "Hummel009's Shuttle Bot"
		defaultCloseOperation = EXIT_ON_CLOSE

		setBounds(0, 0, 600, 900)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = GridLayout(0, 1, 0, 0)
		contentPane = contentPanel

		val radioPanel = createRadioPanel()
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

		contentPanel.add(radioPanel)
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
			ClientsService.unlock(phoneField.text)

			ReservationsService.postBook(
				cache,
				phoneField.text,
				tokenField.text,
				timesDropdown.getSelectedItemString(),
				stopsFromNamesDropdown.getSelectedItemString(),
				stopsToNamesDropdown.getSelectedItemString()
			)

			startButton.isEnabled = false

			stopsToNamesDropdown.isEnabled = false
			startButton.isEnabled = false

			timeRadioButton.isEnabled = false
			loopRadioButton.isEnabled = false

			shutdownCheckbox.isEnabled = false
			exitCheckbox.isEnabled = false
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

		dateField.text = LocalDate.now().plusDays(2).format(formatter)

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

	private fun createRadioPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		timeRadioButton.addActionListener {
			loopRadioButton.isSelected = false
		}
		loopRadioButton.addActionListener {
			timeRadioButton.isSelected = false
		}

		timeRadioButton.isSelected = true

		panel.add(timeRadioButton)
		panel.add(loopRadioButton)

		return panel
	}
}