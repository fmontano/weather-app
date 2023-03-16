package com.fmontano.weather.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeolocationModel(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)
