package com.github.hummel.shuttle

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import com.github.hummel.shuttle.service.CitiesService
import com.github.hummel.shuttle.service.ClientsService
import com.github.hummel.shuttle.service.TransfersService
import java.awt.EventQueue
import java.awt.GridLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.thread

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

	var offPc = false
	var offBot = false
	var timerMode = true
	var date = LocalDate.now().format(formatter)
	var phone = "+375296186183"
	var token =
		"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZSI6IiszNzUyOTYxODYxODMiLCJyb2xlIjoiY2xpZW50IiwiaWF0IjoxNzI4OTIzMDM0fQ.qfks2dNmBB2XBPkAQMJBDNtePgA_Ci3K2wl5B5MMvYU"

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
		val refreshCitiesFromPanel = createRefreshCitiesFromPanel()
		val citiesFromPanel = createCitiesFromPanel()
		val refreshCitiesToPanel = createRefreshCitiesToPanel()
		val citiesToPanel = createCitiesToPanel()
		val datePanel = createDatePanel()
		val refreshTimesPanel = createRefreshTimesFromPanel()
		val timePanel = createTimePanel()
		val refreshStopsFromPanel = createRefreshStopsFromPanel()
		val stopsFromPanel = createStopsFromPanel()
		val refreshStopsToPanel = createRefreshStopsToPanel()
		val stopsToPanel = createStopsToPanel()

		val saveButton = JButton("Запуск")
		saveButton.addActionListener {
			thread {
				//process(data)
			}
		}

		contentPanel.add(radioPanel)
		contentPanel.add(checkboxPanel)
		contentPanel.add(tokenPanel)
		contentPanel.add(phonePanel)
		contentPanel.add(datePanel)
		contentPanel.add(refreshCitiesFromPanel)
		contentPanel.add(citiesFromPanel)
		contentPanel.add(refreshCitiesToPanel)
		contentPanel.add(citiesToPanel)
		contentPanel.add(refreshTimesPanel)
		contentPanel.add(timePanel)
		contentPanel.add(refreshStopsFromPanel)
		contentPanel.add(stopsFromPanel)
		contentPanel.add(refreshStopsToPanel)
		contentPanel.add(stopsToPanel)
		contentPanel.add(saveButton)

		setLocationRelativeTo(null)
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

	private fun createRefreshStopsToPanel(): JButton {
		refreshStopsToButton.isEnabled = false
		refreshStopsToButton.addActionListener {
			ClientsService.unlock(phone)

			stopsToNames = TransfersService.getStopsToNames(
				cache, timesDropdown.getSelectedItemString(), stopsFromNamesDropdown.getSelectedItemString()
			)

			stopsToNamesDropdown.removeAllItems()
			stopsToNames.forEach { stopsToNamesDropdown.addItem(it) }

			stopsToNamesDropdown.selectedItem = stopsToNames[0]
			stopsToNamesDropdown.isEnabled = true

			refreshStopsToButton.isEnabled = false

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

	private fun createRefreshStopsFromPanel(): JButton {
		refreshStopsFromButton.isEnabled = false
		refreshStopsFromButton.addActionListener {
			ClientsService.unlock(phone)

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

	private fun createRefreshTimesFromPanel(): JButton {
		refreshTimesFromButton.isEnabled = false
		refreshTimesFromButton.addActionListener {
			ClientsService.unlock(phone)

			times = TransfersService.getTimes(
				cache,
				phone,
				date,
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

	private fun createRefreshCitiesToPanel(): JButton {
		refreshCitiesToButton.isEnabled = false
		refreshCitiesToButton.addActionListener {
			ClientsService.unlock(phone)

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

	private fun createRefreshCitiesFromPanel(): JButton {
		refreshCitiesFromButton.addActionListener {
			ClientsService.unlock(phone)

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
		dateField.addCaretListener {
			date = dateField.text
		}

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
		tokenField.addCaretListener {
			token = tokenField.text
		}

		panel.add(left)
		panel.add(tokenField)

		return panel
	}

	private fun createPhonePanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JLabel("Номер телефона:")

		phoneField.text = "+375296186182"
		phoneField.addCaretListener {
			phone = phoneField.text
		}

		panel.add(left)
		panel.add(phoneField)

		return panel
	}

	private fun createCheckboxPanel(): JPanel {
		val panel = JPanel()
		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JCheckBox("Гибернация ПК")
		val right = JCheckBox("Выключение бота")

		left.addActionListener {
			offPc = left.isSelected
		}
		right.addActionListener {
			offBot = right.isSelected
		}

		left.isSelected = false
		right.isSelected = false

		panel.add(left)
		panel.add(right)

		return panel
	}

	private fun createRadioPanel(): JPanel {
		val panel = JPanel()

		panel.layout = GridLayout(0, 2, 5, 5)

		val left = JRadioButton("Таймер (заказ ночью)")
		val right = JRadioButton("Мониторинг (попытки раз в минуту)")

		left.addActionListener {
			timerMode = true
			right.isSelected = false
		}
		right.addActionListener {
			timerMode = false
			left.isSelected = false
		}

		left.isSelected = true

		panel.add(left)
		panel.add(right)

		return panel
	}
}