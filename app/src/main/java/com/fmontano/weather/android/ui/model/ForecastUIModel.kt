package com.fmontano.weather.android.ui.model

data class ForecastUIModel(
    val id: Long,
    val iconUrl: String,
    val time: String,
    val condition: String,
    val weather: String
)
