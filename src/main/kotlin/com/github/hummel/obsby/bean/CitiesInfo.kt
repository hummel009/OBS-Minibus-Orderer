package com.github.hummel.obsby.bean

data class CitiesInfo(
	val from: FromCity, val to: List<ToCity>
) {
	data class FromCity(
		val name: String, val id: String, val area: String, val region: String
	)

	data class ToCity(
		val name: String, val id: String, val area: String, val region: String
	)
}