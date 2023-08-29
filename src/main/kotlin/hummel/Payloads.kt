package hummel

fun payloadTransfersInfo(fromCityID: String, toCityID: String, config: Config): String {
	return """
		{
			"from": "$fromCityID",
			"to": "$toCityID",
			"date": "${config.date}",
			"client": "${config.phone}"
		}
	""".trimIndent()
}

fun payloadOrderTicket(fromStopID: String, toStopID: String, timeID: String, config: Config): String {
	return """
		{
			"client": "${config.phone}",
			"transfer": "$timeID",
			"from": "$fromStopID",
			"to": "$toStopID",
			"amount": 1,
			"info": "",
			"createdBy": {
				"role": "web-client",
				"phone": "${config.phone}"
			}
		}
	""".trimIndent()
}