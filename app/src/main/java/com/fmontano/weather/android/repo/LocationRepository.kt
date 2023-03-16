package com.fmontano.weather.android.repo

import com.fmontano.weather.android.utils.WeatherLocationManager

/**
 * Location repository that gets the user's current location from the [WeatherLocationManager] and
 * emits it's results in a Flow.
 */
class LocationRepository(
    private val weatherLocationManager: WeatherLocationManager
) {

    fun getLocation() = weatherLocationManager.locationFlow()
}
