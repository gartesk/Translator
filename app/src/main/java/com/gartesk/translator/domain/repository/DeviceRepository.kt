package com.gartesk.translator.domain.repository

import com.gartesk.translator.domain.entity.Language
import io.reactivex.Single

interface DeviceRepository {

	fun getLanguage(): Single<Language>
}