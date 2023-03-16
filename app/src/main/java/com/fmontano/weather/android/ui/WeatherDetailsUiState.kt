package com.fmontano.weather.android.ui

import com.fmontano.weather.android.ui.model.GeoLocationUIModel
import com.fmontano.weather.android.ui.model.WeatherUIModel

/**
 * Represents the UI state for the Weather details screen
 */
data class WeatherDetailsUiState(
    val showSearchProgressView: Boolean = false,
    val showSearchEmptyView: Boolean = true,
    val showLoadingWeatherProgressView: Boolean = true,
    val showEmptyState: Boolean = false,
    val showError: Boolean = false,
    val searchResults: List<GeoLocationUIModel> = emptyList(),
    val weather: WeatherUIModel? = null,
    val unitMeasurementSystem: UnitMeasurementSystem = UnitMeasurementSystem.IMPERIAL
)
