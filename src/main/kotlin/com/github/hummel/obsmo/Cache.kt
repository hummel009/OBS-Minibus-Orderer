package com.github.hummel.obsmo

import com.github.hummel.obsmo.bean.CitiesInfo
import com.github.hummel.obsmo.bean.TransfersInfo

class Cache {
	lateinit var citiesInfo: Array<CitiesInfo>
	lateinit var transfersInfo: Array<TransfersInfo>
	var transfersInfoPseudo: Boolean = false
}