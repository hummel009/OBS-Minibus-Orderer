package hummel

import java.time.LocalTime

fun calculateDelayUntil(hour: Int, minute: Int, second: Int): Long {
	val currentTime = LocalTime.now()
	val targetTime = LocalTime.of(hour, minute, second)
	var delay = targetTime.toSecondOfDay() - currentTime.toSecondOfDay()
	if (delay < 0) {
		delay += 24 * 60 * 60
	}
	return delay * 1000L
}