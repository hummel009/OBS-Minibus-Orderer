package hummel

import com.google.gson.Gson

fun getIDsBetweenCities(responseBetweenCities: String, time: String): Triple<String, String, String> {
	val bookings = Gson().fromJson(responseBetweenCities, Array<Booking>::class.java).toList()

	var fromID = ""
	var toID = ""
	var timeID = ""

	loop@ for (booking in bookings) {
		for ((from, to) in booking.stopsForBooking) {
			if (from.name == stopFrom && from.time == time) {
				for ((name, _, id, _, _) in to) {
					if (name == stopTo) {
						fromID = from.id
						toID = id
						timeID = booking.id
						break@loop
					}
				}
			}
		}
	}

	return Triple(fromID, toID, timeID)
}

fun getIDsForBooking(responseBooking: String): Pair<String, String> {
	var fromID = ""
	var toID = ""

	val cities = Gson().fromJson(responseBooking, Array<City>::class.java).toList()
	for ((from, _) in cities) {
		if (from.name == cityFrom) {
			fromID = from.id
		} else if (from.name == cityTo) {
			toID = from.id
		}
	}

	return Pair(fromID, toID)
}