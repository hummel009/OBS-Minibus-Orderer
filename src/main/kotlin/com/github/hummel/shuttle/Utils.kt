package com.github.hummel.shuttle

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun calculateTargetTime(hour: Int, minute: Int, second: Int): Long {
	val currentTime = System.currentTimeMillis()
	val now = Instant.ofEpochMilli(currentTime)
	val targetTime = LocalDate.now().atTime(hour, minute, second).atZone(ZoneId.systemDefault()).toInstant()

	if (targetTime <= now) {
		return targetTime.plus(1, ChronoUnit.DAYS).toEpochMilli()
	}

	return targetTime.toEpochMilli()
}