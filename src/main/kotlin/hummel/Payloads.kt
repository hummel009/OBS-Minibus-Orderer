package hummel

fun payloadTransfersInfo(fromCityID: String, toCityID: String, data: Data): String {
	return """
		{
			"from": "$fromCityID",
			"to": "$toCityID",
			"date": "${data.date}",
			"client": "${data.phone}"
		}
		""".trimIndent()
}

fun payloadOrderTicket(fromStopID: String, toStopID: String, timeID: String, data: Data): String {
	return """
		{
			"client": "${data.phone}",
			"transfer": "$timeID",
			"from": "$fromStopID",
			"to": "$toStopID",
			"amount": 1,
			"info": "",
			"createdBy": {
				"role": "web-client",
				"phone": "${data.phone}"
			}
		}
		""".trimIndent()
}