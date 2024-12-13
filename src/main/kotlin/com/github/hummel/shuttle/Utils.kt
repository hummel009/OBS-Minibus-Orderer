package com.github.hummel.shuttle

import com.google.gson.Gson
import javax.swing.JComboBox

val gson: Gson = Gson()

fun <E> JComboBox<E>.getSelectedItemString(): String = selectedItem!!.toString()