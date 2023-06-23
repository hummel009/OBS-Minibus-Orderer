package hummel

fun payloadBetweenCities(fromCityID: String, toCityID: String, date: String): String {
	return """
		{
	        "from": "$fromCityID",
	        "to": "$toCityID",
	        "date": "$date",
	        "client": "$phone"
		}
	""".trimIndent()
}

fun payloadBook(fromStopID: String, toStopID: String, timeID: String): String {
	return """
        {
            "client": "$phone",
            "transfer": "$timeID",
            "from": "$fromStopID",
            "to": "$toStopID",
            "amount": 1,
            "info": "",
            "createdBy": {
                "role": "web-client",
                "phone": "$phone"
            }
        }
    """.trimIndent()
}