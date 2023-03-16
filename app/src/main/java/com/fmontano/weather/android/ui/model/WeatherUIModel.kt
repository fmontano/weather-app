package com.fmontano.weather.android.ui.model

data class WeatherUIModel(
    val location: LocationWeatherUIModel,
    val forecast: List<ForecastUIModel>?
)
