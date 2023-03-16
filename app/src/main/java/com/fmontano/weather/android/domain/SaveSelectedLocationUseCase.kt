package com.fmontano.weather.android.domain

import com.fmontano.weather.android.repo.SharedPreferencesRepository
import com.fmontano.weather.android.ui.model.GeoLocationUIModel

/**
 * Use case that saves user's last location search in user's device via shared preferences.
 */
class SaveSelectedLocationUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    operator fun invoke(location: GeoLocationUIModel) {
        if (location.displayName.isBlank().not()) {
            sharedPreferencesRepository.setLastSearchedLocation(location)
        }
    }
}
