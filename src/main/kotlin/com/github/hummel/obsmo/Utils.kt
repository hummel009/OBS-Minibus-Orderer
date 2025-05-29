package com.github.hummel.obsmo

import com.google.gson.Gson
import java.time.format.DateTimeFormatter
import javax.swing.JComboBox

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

val gson: Gson = Gson()

fun <E> JComboBox<E>.getSelectedItemString(): String = selectedItem!!.toString()