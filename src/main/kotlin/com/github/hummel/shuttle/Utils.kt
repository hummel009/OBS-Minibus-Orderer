package com.github.hummel.shuttle

import javax.swing.JComboBox

fun <E> JComboBox<E>.getSelectedItemString(): String = selectedItem!!.toString()