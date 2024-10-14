package com.github.hummel.shuttle.service

import com.github.hummel.shuttle.dao.ReservationsDao

object ReservationsService {
	fun postBook(phone: String, token: String, fromStopId: String, toStopId: String, time: String) {
		ReservationsDao.postBook(phone, token, fromStopId, toStopId, time)
	}
}