package com.gartesk.translator.data.device

import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.repository.DeviceRepository
import io.reactivex.Single
import java.util.*

class AndroidDeviceRepository : DeviceRepository {

	override fun getLanguage(): Single<Language> =
		Single.fromCallable { Language(Locale.getDefault().language) }
}