package hummel

import java.util.*

fun calculateTargetTime(hour: Int, minute: Int, second: Int): Long {
	val currentTime = System.currentTimeMillis()
	val calendar = Calendar.getInstance()
	calendar.set(Calendar.HOUR_OF_DAY, hour)
	calendar.set(Calendar.MINUTE, minute)
	calendar.set(Calendar.SECOND, second)

	if (calendar.timeInMillis <= currentTime) {
		calendar.add(Calendar.DAY_OF_MONTH, 1)
	}

	return calendar.timeInMillis
}