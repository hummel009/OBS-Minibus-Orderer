package hummel

import com.google.gson.Gson

fun getIsTicketNotOrdered(responseUserInfo: String): Boolean {
	val userInfo = Gson().fromJson(responseUserInfo, SUserInfo::class.java)

	for (reservation in userInfo.reservations) {
		if (reservation.from.name == stopFrom && reservation.from.time == time && reservation.date == date) {
			return false
		}
	}
	return true
}

fun getTransferIDs(responseBetweenCities: String, time: String): Triple<String, String, String> {
	val transfersInfo = Gson().fromJson(responseBetweenCities, Array<STransferInfo>::class.java).toList()

	var fromID = ""
	var toID = ""
	var timeID = ""

	loop@ for (transferInfo in transfersInfo) {
		for ((from, to) in transferInfo.stopsForBooking) {
			if (from.name == stopFrom && from.time == time) {
				for ((name, _, id, _, _) in to) {
					if (name == stopTo) {
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

fun getBookingIDs(responseBooking: String): Pair<String, String> {
	var fromID = ""
	var toID = ""

	val bookingsInfo = Gson().fromJson(responseBooking, Array<SBookingInfo>::class.java).toList()
	for ((from, _) in bookingsInfo) {
		if (from.name == cityFrom) {
			fromID = from.id
		} else if (from.name == cityTo) {
			toID = from.id
		}
	}

	return Pair(fromID, toID)
}