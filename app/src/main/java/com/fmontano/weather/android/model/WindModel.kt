package com.fmontano.weather.android.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WindModel(
    val speed: Float,
    @Json(name = "deg")
    val degrees: Int
)
