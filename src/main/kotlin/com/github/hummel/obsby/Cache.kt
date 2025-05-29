package com.github.hummel.obsby

import com.github.hummel.obsby.bean.CitiesInfo
import com.github.hummel.obsby.bean.TransfersInfo

class Cache {
	lateinit var citiesInfo: Array<CitiesInfo>
	lateinit var transfersInfo: Array<TransfersInfo>
	var transfersInfoPseudo: Boolean = false
}