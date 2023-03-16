package com.fmontano.weather.android.data

import com.fmontano.weather.android.ui.model.ForecastUIModel
import com.fmontano.weather.android.ui.model.LocationWeatherUIModel
import com.fmontano.weather.android.ui.model.WeatherUIModel
import java.util.UUID

val weatherWithForecast = WeatherUIModel(
    location = LocationWeatherUIModel(
        id = UUID.randomUUID().toString(),
        name = "San Francisco",
        humidity = "12%",
        windSpeed = "12 NW",
        minTemp = "12",
        maxTemp = "423",
        currentTemp = "30",
        iconUrl = "https://coolimages/icon",
        currentConditions = "Cloudy with a chance of meatballs",
        feelsLike = "cold"
    ),
    forecast = listOf(
        ForecastUIModel(
            id = Math.random().toLong(),
            weather = "Cold ",
            iconUrl = "https://coolimages/icon",
            condition = "Cloudy",
            time = "Right now"
        )
    )
)
