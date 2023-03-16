package com.fmontano.weather.android.repo

import com.fmontano.weather.android.model.ForecastResponse
import com.fmontano.weather.android.model.WeatherResponse
import com.fmontano.weather.android.network.WeatherService
import retrofit2.Response

/**
 * Repository responsible for loading weather information and weather forecast for a given location
 * and applying any transformation to the data needed by the use cases. In this case, it's pretty
 * straightforward and we just pass the data as we get it.
 *
 * @param weatherService The weather service that performs the API call
 * @param apiKey The API key for the weather service
 */
class WeatherRepository(
    private val weatherService: WeatherService,
    private val apiKey: String
) {

    /**
     * Gets the weather information for a given location
     *
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @param units The unit of measurement for the response. We only support Metric and Imperial
     *              right now
     */
    suspend fun getLocationWeather(
        lat: Double,
        lon: Double,
        units: String
    ): Response<WeatherResponse> {
        return weatherService.getLocationWeather(
            lat = lat,
            lon = lon,
            apiKey = apiKey,
            units = units
        )
    }

    /**
     * Gets the forecast information for a given location in 3 hour increments
     *
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @param units The unit of measurement for the response. We only support Metric and Imperial
     *              right now
     * @param itemCount the number of results to fetch.
     */
    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        itemCount: Int
    ): Response<ForecastResponse> {
        return weatherService.getForecast(
            lat = lat,
            lon = lon,
            apiKey = apiKey,
            units = units,
            itemCount = itemCount
        )
    }
}
