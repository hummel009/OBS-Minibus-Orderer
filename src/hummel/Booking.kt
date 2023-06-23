package hummel

data class Booking(
	val id: String,
	val date: String,
	val bookingEnabled: Boolean,
	val onlyDispatcher: Boolean,
	val hiddenFromDispatcher: Boolean,
	val route: Route,
	val from: From,
	val to: To,
	val carrier: Carrier,
	val car: Car,
	val limit: Limit,
	val amount: Int,
	val stopsForBooking: List<StopsForBooking>,
	val discount: Int,
	val price: Double,
	val routeDiscount: Int,
	val dynamicDiscount: DynamicDiscount
) {
	data class From(
		val stop: String, val name: String, val id: String, val city: City2, val time: String
	)

	data class To(
		val stop: String, val name: String, val id: String, val city: City2, val time: String
	)

	data class City2(
		val id: String, val name: String
	)

	data class StopsForBooking(
		val from: Stop, val to: List<Stop>
	)


	data class Route(
		val id: String, val name: String, val number: String, val limits: List<Limit>
	)

	data class Stop(
		val name: String, val city: String, val id: String, val cityId: String, val time: String
	)

	data class Carrier(
		val name: String,
		val hidden: Boolean,
		val description: String,
		val info: String,
		val contacts: List<Contact>,
		val createdAt: String,
		val updatedAt: String,
		val addressBook: String,
		val addressLegal: String,
		val licenseNumber: String,
		val unp: String,
		val legalPhone: String,
		val licenseIssueDate: String,
		val id: String
	)

	data class Car(
		val number: String,
		val amount: Int,
		val description: String,
		val carrier: String,
		val hidden: Boolean,
		val createdAt: String,
		val updatedAt: String,
		val id: String
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
	)

	data class DynamicDiscount(
		val data: List<Any>
	)

	data class Contact(
		val route: RouteInfo, val phone: String, val link: String
	)

	data class RouteInfo(
		val name: String, val number: String, val id: String
	)
}