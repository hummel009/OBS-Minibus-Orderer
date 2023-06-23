package hummel

data class City(
	val from: From,
	val to: List<To>
) {
	data class From(
		val name: String,
		val id: String,
		val area: String,
		val region: String
	)

	data class To(
		val name: String,
		val id: String,
		val area: String,
		val region: String
	)
}