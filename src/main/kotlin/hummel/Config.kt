package hummel

data class Config(
	val orderingTime: Triple<Int, Int, Int>,
	val phone: String,
	val date: String,
	val time: String,
	val stopFrom: String,
	val stopTo: String,
	val cityFrom: String,
	val cityTo: String,
	val token: String
)