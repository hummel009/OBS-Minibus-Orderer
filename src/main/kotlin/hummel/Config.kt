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

val defaultConfig: Config = Config(
	Triple(3, 1, 0),
	"+375296186182",
	"2023-09-04",
	"07:45",
	"РДК",
	"ст.м.Восток",
	"Логойск",
	"Минск",
	"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZSI6IiszNzUyOTYxODYxODIiLCJyb2xlIjoiY2xpZW50IiwiaWF0IjoxNjg3MzU0NTQwLCJleHAiOjE2ODc0NDA5NDB9.v3wQlCAPDnQ6XeVFSz0Ez8px4nOstUM3sR10cm_oivw"
)