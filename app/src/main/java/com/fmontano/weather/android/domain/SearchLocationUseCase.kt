package com.fmontano.weather.android.domain

import com.fmontano.weather.android.model.GeolocationModel
import com.fmontano.weather.android.repo.GeolocationRepository
import com.fmontano.weather.android.ui.model.GeoLocationUIModel
import com.fmontano.weather.android.utils.DispatcherProvider
import kotlinx.coroutines.withContext

/**
 * Use case that performs a location search given a search query
 */
class SearchLocationUseCase(
    private val dispatcherProvider: DispatcherProvider,
    private val geolocationRepository: GeolocationRepository
) {

    // TODO I'd also use [ResponseRequest] here to send back a proper error instead of null.
    //    I'm ignoring it right now since I'm running out to time to add an error message in the
    //    location search view
    suspend operator fun invoke(searchQuery: String): List<GeoLocationUIModel>? {
        return withContext(dispatcherProvider.io) {
            val response = geolocationRepository.getGeolocation(searchQuery)

            if (response.isSuccessful) {
                response.body()?.map { location ->
                    GeoLocationUIModel(
                        displayName = generateDisplayName(location),
                        lat = location.lat,
                        lon = location.lon
                    )
                }
            } else {
                null
            }
        }
    }

    private fun generateDisplayName(location: GeolocationModel): String {
        var displayName = location.name
        location.state?.let {
            displayName += ", ${location.state}"
        }
        displayName += ", ${location.country}"
        return displayName
    }
}
