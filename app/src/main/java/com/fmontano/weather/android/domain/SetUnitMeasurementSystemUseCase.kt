package com.fmontano.weather.android.domain

import com.fmontano.weather.android.repo.SharedPreferencesRepository
import com.fmontano.weather.android.ui.UnitMeasurementSystem
import com.fmontano.weather.android.ui.WeatherViewModel

/**
 * Use case that saves user's last units of measurement preferences
 */
class SetUnitMeasurementSystemUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    operator fun invoke(unitMeasurementSystem: UnitMeasurementSystem) =
        sharedPreferencesRepository.setUnitMeasurementSystem(unitMeasurementSystem.name.lowercase())
}
