package com.fmontano.weather.android.ui.model

data class LocationWeatherUIModel(
    val id: String,
    val name: String?,
    val currentTemp: String,
    val iconUrl: String,
    val feelsLike: String,
    val currentConditions: String?,
    val minTemp: String,
    val maxTemp: String,
    val windSpeed: String,
    val humidity: String
)
