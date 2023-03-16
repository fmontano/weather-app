package com.fmontano.weather.android.repo

import android.content.SharedPreferences
import com.fmontano.weather.android.ui.model.GeoLocationUIModel

class SharedPreferencesRepository(
    private val sharedPreferences: SharedPreferences
) {
    private val sharedPreferencesKeyLat = "lat"
    private val sharedPreferencesKeyLon = "lon"
    private val sharedPreferencesKeyDisplayName = "displayName"
    private val sharedPreferencesKeyUnits = "units"

    fun setLastSearchedLocation(location: GeoLocationUIModel) {
        sharedPreferences.edit()
            .putLong(sharedPreferencesKeyLat, location.lat.toBits())
            .putLong(sharedPreferencesKeyLon, location.lon.toBits())
            .putString(sharedPreferencesKeyDisplayName, location.displayName)
            .apply()
    }

    fun getLastSearchedLocation(): GeoLocationUIModel? {
        val lat = Double.fromBits(sharedPreferences.getLong(sharedPreferencesKeyLat, Long.MIN_VALUE))
        val lon = Double.fromBits(sharedPreferences.getLong(sharedPreferencesKeyLon, Long.MIN_VALUE))
        val displayName = sharedPreferences.getString(sharedPreferencesKeyDisplayName, null)

        return if (displayName == null || lat.isNaN() || lon.isNaN()) {
            null
        } else {
            GeoLocationUIModel(
                lat = lat,
                lon = lon,
                displayName = displayName
            )
        }
    }

    fun setUnitMeasurementSystem(unitMeasurementSystem: String) {
        sharedPreferences.edit()
            .putString(sharedPreferencesKeyUnits, unitMeasurementSystem)
            .apply()
    }

    fun getUnitMeasurementSystem(): String? =
        sharedPreferences.getString(sharedPreferencesKeyUnits, null)
}
