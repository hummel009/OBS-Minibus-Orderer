package hummel

import com.google.gson.Gson

fun getIsTicketNotOrdered(responseUserInfo: String, data: Data): Boolean {
	val userInfo = Gson().fromJson(responseUserInfo, SUserInfo::class.java)

	return userInfo.reservations.none {
		it.from.name == data.stopFrom && it.from.time == data.time && it.date == data.date
	}
}

fun getTransferIDs(responseBetweenCities: String, data: Data): Triple<String, String, String> {
	val transfersInfo = Gson().fromJson(responseBetweenCities, Array<STransferInfo>::class.java).toList()

	var fromID = ""
	var toID = ""
	var timeID = ""

	loop@ for (transferInfo in transfersInfo) {
		for ((from, to) in transferInfo.stopsForBooking) {
			if (from.name == data.stopFrom && from.time == data.time) {
				for ((name, _, id, _, _) in to) {
					if (name == data.stopTo) {
						fromID = from.id
						toID = id
						timeID = transferInfo.id
						break@loop
					}
				}
			}
		}
	}

	return Triple(fromID, toID, timeID)
}

fun getBookingIDs(responseBooking: String, data: Data): Pair<String, String> {
	var fromID = ""
	var toID = ""

	val bookingsInfo = Gson().fromJson(responseBooking, Array<SBookingInfo>::class.java).toList()
	for ((from, _) in bookingsInfo) {
		if (from.name == data.cityFrom) {
			fromID = from.id
		} else if (from.name == data.cityTo) {
			toID = from.id
		}
	}

	return fromID to toID
}