package com.fmontano.weather.android.domain

import com.fmontano.weather.android.repo.SharedPreferencesRepository
import com.fmontano.weather.android.ui.model.GeoLocationUIModel

/**
 * Use case that gets user's last location search in user's device via shared preferences.
 */
class GetLastSavedLocationUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    operator fun invoke(): GeoLocationUIModel? = sharedPreferencesRepository.getLastSearchedLocation()
}
