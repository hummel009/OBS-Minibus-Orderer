package com.github.hummel.obsmo

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme
import com.github.hummel.obsmo.service.CitiesService
import com.github.hummel.obsmo.service.ClientsService
import com.github.hummel.obsmo.service.ReservationsService
import com.github.hummel.obsmo.service.TransfersService
import java.awt.BorderLayout
import java.awt.Dimension
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
			val frame = MinibusOrderer()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class MinibusOrderer : JFrame() {
	private val cache: Cache = Cache()

	private val phoneField: JTextField = JTextField()
	private val tokenField: JTextField = JTextField()
	private val dateField: JTextField = JTextField()

	private val citiesFromCombo: JComboBox<String> = JComboBox<String>()
	private val citiesToCombo: JComboBox<String> = JComboBox<String>()
	private val stopsFromCombo: JComboBox<String> = JComboBox<String>()
	private val stopsToCombo: JComboBox<String> = JComboBox<String>()
	private val timesCombo: JComboBox<String> = JComboBox<String>()

	private val refreshCitiesFrom: JButton = JButton("Обновить")
	private val refreshCitiesTo: JButton = JButton("Обновить")
	private val refreshTimes: JButton = JButton("Обновить")
	private val refreshStopsFrom: JButton = JButton("Обновить")
	private val refreshStopsTo: JButton = JButton("Обновить")

	private val start: JButton = JButton("Запуск бота")

	private val shutdownCheck: JCheckBox = JCheckBox("Гибернация ПК")
	private val exitCheck: JCheckBox = JCheckBox("Выключение бота")

	init {
		title = "Hummel009's Minibus Orderer"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(100, 100, 600, 540)

		val contentPanel = JPanel().apply {
			border = EmptyBorder(10, 10, 10, 10)
			layout = GridLayout(0, 1, 5, 10)
		}

		contentPanel.add(createCheckboxPanel())
		contentPanel.add(createInputPanel("Номер телефона:", phoneField))
		contentPanel.add(createInputPanel("Токен:", tokenField))
		contentPanel.add(createInputPanel("Дата отправки:", dateField.apply {
			text = LocalDate.now().format(formatter)
		}))

		contentPanel.add(
			createButtonComboPanel(
				"Город отправки:", citiesFromCombo, refreshCitiesFrom, ::updateCitiesFrom
			)
		)
		contentPanel.add(
			createButtonComboPanel(
				"Город прибытия:", citiesToCombo, refreshCitiesTo, ::updateCitiesTo
			)
		)
		contentPanel.add(
			createButtonComboPanel(
				"Время отправки:", timesCombo, refreshTimes, ::updateTimes
			)
		)
		contentPanel.add(
			createButtonComboPanel(
				"Остановка отправки:", stopsFromCombo, refreshStopsFrom, ::updateStopsFrom
			)
		)
		contentPanel.add(
			createButtonComboPanel(
				"Остановка прибытия:", stopsToCombo, refreshStopsTo, ::updateStopsTo
			)
		)
		contentPanel.add(start.apply {
			isEnabled = false
			addActionListener { startOrderingProcess() }
		})

		contentPane = contentPanel

		refreshCitiesFrom.isEnabled = true

		setLocationRelativeTo(null)
	}

	private fun createCheckboxPanel(): JPanel {
		return JPanel(GridLayout(1, 2)).apply {
			add(shutdownCheck)
			add(exitCheck)
		}
	}

	private fun createInputPanel(label: String, field: JTextField): JPanel {
		return JPanel(BorderLayout(5, 0)).apply {
			add(JLabel(label).apply {
				preferredSize = Dimension(150, preferredSize.height)
			}, BorderLayout.WEST)
			add(field, BorderLayout.CENTER)
		}
	}

	private fun createButtonComboPanel(
		label: String, combo: JComboBox<String>, button: JButton, butonListener: () -> Unit
	): JPanel {
		return JPanel(BorderLayout(5, 0)).apply {
			add(JLabel(label).apply {
				preferredSize = Dimension(150, preferredSize.height)
			}, BorderLayout.WEST)
			add(button, BorderLayout.CENTER)
			add(combo.apply {
				preferredSize = Dimension(250, preferredSize.height)
			}, BorderLayout.EAST)
			button.isEnabled = false
			combo.isEnabled = false
			button.addActionListener { butonListener.invoke() }
		}
	}


	private fun updateCitiesFrom() {
		val citiesFromNames = CitiesService.getCitiesFromNames(cache)
		citiesFromCombo.model = DefaultComboBoxModel(citiesFromNames)
		citiesFromCombo.isEnabled = true
		citiesFromCombo.selectedIndex = 0
		refreshCitiesFrom.isEnabled = false
		refreshCitiesTo.isEnabled = true
	}

	private fun updateCitiesTo() {
		val citiesToNames = CitiesService.getCitiesToNames(
			cache, citiesFromCombo.getSelectedItemString()
		)
		citiesToCombo.model = DefaultComboBoxModel(citiesToNames)
		citiesToCombo.isEnabled = true
		citiesToCombo.selectedIndex = 0
		refreshCitiesTo.isEnabled = false
		citiesFromCombo.isEnabled = false
		refreshTimes.isEnabled = true
	}

	private fun updateTimes() {
		val times = TransfersService.getTimes(
			cache,
			phoneField.text,
			dateField.text,
			citiesFromCombo.getSelectedItemString(),
			citiesToCombo.getSelectedItemString()
		)
		timesCombo.model = DefaultComboBoxModel(times)
		timesCombo.isEnabled = true
		timesCombo.selectedIndex = 0
		refreshTimes.isEnabled = false
		citiesToCombo.isEnabled = false
		refreshStopsFrom.isEnabled = true
	}

	private fun updateStopsFrom() {
		val stopsFromNames = TransfersService.getStopsFromNames(
			cache, timesCombo.getSelectedItemString()
		)
		stopsFromCombo.model = DefaultComboBoxModel(stopsFromNames)
		stopsFromCombo.isEnabled = true
		stopsFromCombo.selectedIndex = 0
		refreshStopsFrom.isEnabled = false
		timesCombo.isEnabled = false
		refreshStopsTo.isEnabled = true
	}

	private fun updateStopsTo() {
		val stopsToNames = TransfersService.getStopsToNames(
			cache, timesCombo.getSelectedItemString(), stopsFromCombo.getSelectedItemString()
		)
		stopsToCombo.model = DefaultComboBoxModel(stopsToNames)
		stopsToCombo.isEnabled = true
		stopsToCombo.selectedIndex = 0
		refreshStopsTo.isEnabled = false
		stopsFromCombo.isEnabled = false
		start.isEnabled = true
	}

	private fun startOrderingProcess() {
		stopsToCombo.isEnabled = false

		thread {
			try {
				val pause = 60
				loop@ while (true) {
					val currentTime = LocalTime.now(ZoneId.systemDefault())
					val time = "%02d:%02d".format(currentTime.hour, currentTime.minute)

					val notOrdered = ClientsService.isTicketNotOrdered(
						cache = cache,
						phone = phoneField.text,
						token = tokenField.text,
						date = dateField.text,
						cityFromName = citiesFromCombo.getSelectedItemString(),
						cityToName = citiesToCombo.getSelectedItemString(),
						time = timesCombo.getSelectedItemString(),
						stopFromName = stopsFromCombo.getSelectedItemString()
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
							time = timesCombo.getSelectedItemString(),
							stopFromName = stopsFromCombo.getSelectedItemString(),
							stopToName = stopsToCombo.getSelectedItemString()
						)
						println("[$time] Попытка завершена. Следующая попытка через $pause секунд.")
						Thread.sleep(pause * 1000L)
						continue@loop
					}

					println("[$time] Билет заказан.")
					if (!exitCheck.isSelected && !shutdownCheck.isSelected) {
						EventQueue.invokeLater {
							JOptionPane.showMessageDialog(
								this, "[$time] Билет заказан.", "Message", JOptionPane.INFORMATION_MESSAGE
							)
						}
					}
					break@loop
				}

				if (exitCheck.isSelected) {
					exitProcess(0)
				}
				if (shutdownCheck.isSelected) {
					hibernatePC()
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	private fun hibernatePC() {
		try {
			val process = ProcessBuilder("rundll32.exe", "powrprof.dll,SetSuspendState", "0,1,0").start()
			if (process.waitFor() == 0) {
				println("PC hibernated successfully")
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

private fun JComboBox<String>.getSelectedItemString(): String = selectedItem?.toString() ?: ""