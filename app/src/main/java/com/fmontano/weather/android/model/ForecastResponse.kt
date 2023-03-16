package com.fmontano.weather.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    val list: List<ForecastListItemModel>
)
