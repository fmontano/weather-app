package com.fmontano.weather.android.repo

import com.fmontano.weather.android.model.GeolocationModel
import com.fmontano.weather.android.network.GeolocationService
import retrofit2.Response

/**
 * Repository responsible for searching for a location using the Geocoding API and applying any
 * transformation to the data needed by the use cases. In this case, it's pretty
 * straightforward and we just pass the data as we get it.
 *
 * @param geolocationService The geolocation service that performs the API call
 * @param apiKey The API key for the weather service
 */
class GeolocationRepository(
    private val geolocationService: GeolocationService,
    private val apiKey: String
) {

    suspend fun getGeolocation(searchQuery: String): Response<List<GeolocationModel>> {
        return geolocationService.getDirectGeolocation(
            searchQuery = searchQuery,
            apiKey = apiKey
        )
    }
}
