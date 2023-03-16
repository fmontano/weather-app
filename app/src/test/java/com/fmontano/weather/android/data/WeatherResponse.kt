package com.fmontano.weather.android.data

import com.fmontano.weather.android.repo.MainModel
import com.fmontano.weather.android.repo.WeatherResponse
import com.fmontano.weather.android.repo.WindModel
import java.util.Date
import java.util.UUID

val validWindModel = WindModel(
    speed = 12f,
    degrees = 120
)

val validMainModel = MainModel(
    temp = 12f,
    feelsLike = 112f,
    tempMin = 20f,
    tempMax = 220f,
    humidity = 100
)

val successfulWeatherResponse = WeatherResponse(
    id = UUID.randomUUID().toString(),
    weather = listOf(),
    main = validMainModel,
    name = "Some location",
    dt = Date().toInstant().toEpochMilli(),
    wind = validWindModel
)
