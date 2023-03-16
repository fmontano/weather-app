package com.fmontano.weather.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherModel(
    val id: String,
    val main: String,
    val description: String,
    val icon: String
)
