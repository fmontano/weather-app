package com.fmontano.weather.android.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastListItemModel(
    val main: MainModel,
    val weather: List<WeatherModel>,
    @Json(name = "dt_txt")
    val dateTimeText: String,
    @Json(name = "dt")
    val dateTime: Long
)
