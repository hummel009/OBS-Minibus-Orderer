package hummel

import java.time.LocalTime
import java.util.*

fun calculateTargetTime(hour: Int, minute: Int, second: Int): Long {
	val calendar = Calendar.getInstance()
	calendar.set(Calendar.HOUR_OF_DAY, hour)
	calendar.set(Calendar.MINUTE, minute)
	calendar.set(Calendar.SECOND, second)
	calendar.set(Calendar.MILLISECOND, 0)
	val targetTime = calendar.timeInMillis

	if (targetTime <= System.currentTimeMillis()) {
		calendar.add(Calendar.DAY_OF_YEAR, 1)
		return calendar.timeInMillis
	}

	return targetTime
}

fun getCurrentTime(): String {
	val currentTime = LocalTime.now()
	val formattedTime = currentTime.toString()
	return "$formattedTime"
}