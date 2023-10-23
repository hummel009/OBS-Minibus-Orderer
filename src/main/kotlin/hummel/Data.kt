package hummel

data class Data(
	val phone: String,
	val date: String,
	val time: String,
	val stopFrom: String,
	val stopTo: String,
	val cityFrom: String,
	val cityTo: String,
	val token: String,
	val timer: Boolean,
	val shutdown: Boolean,
	val exit: Boolean
)