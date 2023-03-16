package com.fmontano.weather.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val id: String,
    val weather: List<WeatherModel>,
    val main: MainModel,
    val wind: WindModel,
    val name: String?,
    val dt: Long
)
