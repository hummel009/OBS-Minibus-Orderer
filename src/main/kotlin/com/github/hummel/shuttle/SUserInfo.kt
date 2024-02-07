package com.github.hummel.shuttle

data class SUserInfo(
	val name: String,
	val phone: String,
	val surname: String,
	val savedSearches: List<SavedSearch>,
	val absences: List<Absence>,
	val reservations: List<Reservation>
) {
	data class SavedSearch(
		val from: String, val to: String
	)

	data class Absence(
		val id: String,
		val from: FromTo,
		val to: FromTo,
		val route: Route,
		val carrier: Carrier,
		val date: String,
		val time: String,
		val appendedBy: AppendedBy,
		val createdAt: String,
		val reservationInfo: ReservationInfo,
		val dates: Dates
	) {
		data class FromTo(
			val name: String, val city: String
		)

		data class AppendedBy(
			val role: String,
			val phone: String,
		)

		data class ReservationInfo(
			val createdBy: CreatedBy, val createdAt: String
		)

		data class Dates(
			val created: String, val updated: String
		)
	}

	data class Reservation(
		val payed: Boolean,
		val from: FromTo,
		val to: FromTo,
		val route: Route,
		val carrier: Carrier,
		val car: Car,
		val id: String,
		val declined: Boolean,
		val declinedBy: DeclinedBy,
		val places: List<Int>,
		val price: Int,
		val date: String,
		val time: String,
		val limit: Limit,
		val driver: Driver,
		val createdBy: CreatedBy
	) {
		data class FromTo(
			val id: String, val name: String, val city: String, val area: String, val time: String
		)

		data class Car(
			val number: String, val description: String, val amount: String
		)

		data class DeclinedBy(
			val role: String, val phone: String
		)

		data class Limit(
			val route: String,
			val carrier: String,
			val showCar: Boolean,
			val showCarDescription: Boolean,
			val showAmount: Boolean,
			val restrictBooking: Int,
			val showDriverName: Boolean,
			val showDriverSurname: Boolean,
			val showDriverPhone: Boolean,
			val showPlaces: Boolean,
			val timeBeforeBooking: Int,
			val timeBeforeDecline: Int,
			val createdAt: String,
			val updatedAt: String,
			val dynamicDiscount: DynamicDiscount,
			val id: String,
			val bookingEnabled: Boolean
		) {
			data class DynamicDiscount(
				val data: List<Any>
			)
		}

		data class Driver(
			val phone: String,
			val name: String,
			val surname: String,
			val role: String,
			val personalID: String,
			val hidden: Boolean,
			val pin: String,
			val blocked: Boolean,
			val selectedRoute: SelectedRoute,
			val info: String,
			val reviews: List<Any>,
			val createdAt: String,
			val updatedAt: String,
			val blockedLabel: String,
			val fullName: String,
			val roleLabel: String
		) {
			data class SelectedRoute(
				val id: String, val name: String, val number: String
			)
		}
	}

	data class CreatedBy(
		val role: String, val phone: String
	)

	data class Carrier(
		val name: String, val info: String, val description: String, val id: String
	)

	data class Route(
		val id: String, val name: String, val number: String, val limits: List<Limit>
	) {
		data class Limit(
			val route: String,
			val carrier: String,
			val showCar: Boolean,
			val showCarDescription: Boolean,
			val showAmount: Boolean,
			val restrictBooking: Int,
			val showDriverName: Boolean,
			val showDriverSurname: Boolean,
			val showDriverPhone: Boolean,
			val showPlaces: Boolean,
			val timeBeforeBooking: Int,
			val timeBeforeDecline: Int,
			val createdAt: String,
			val updatedAt: String,
			val dynamicDiscount: DynamicDiscount,
			val id: String,
			val bookingEnabled: Boolean
		) {
			data class DynamicDiscount(
				val data: List<Any>
			)
		}
	}
}