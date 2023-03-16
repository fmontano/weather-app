package com.fmontano.weather.android.domain

import com.fmontano.weather.android.repo.SharedPreferencesRepository
import com.fmontano.weather.android.ui.UnitMeasurementSystem

/**
 * Use case that gets user's last units of measurement preferences
 */
class GetUnitMeasurementSystemUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    operator fun invoke(): UnitMeasurementSystem =
        UnitMeasurementSystem.parseString(sharedPreferencesRepository.getUnitMeasurementSystem())

}
