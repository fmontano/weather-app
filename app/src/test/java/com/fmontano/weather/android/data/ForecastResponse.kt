package com.fmontano.weather.android.data

import com.fmontano.weather.android.repo.ForecastListItemModel
import com.fmontano.weather.android.repo.ForecastResponse
import com.fmontano.weather.android.repo.WeatherModel
import java.util.Date
import java.util.UUID

val weatherModel = WeatherModel(
    id = UUID.randomUUID().toString(),
    main = "Very cold",
    description = "Yes. Cold",
    icon = "https://awesomeicons/cold.png"
)

val forecastListItemModel = ForecastListItemModel(
    main = validMainModel,
    weather = listOf(weatherModel),
    dateTime = Date().toInstant().toEpochMilli(),
    dateTimeText = "3 pm"
)

val successfulForecastResponse = ForecastResponse(
    list = listOf(forecastListItemModel)
)
