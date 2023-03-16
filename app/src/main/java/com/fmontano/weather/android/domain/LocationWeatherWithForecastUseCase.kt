package com.fmontano.weather.android.domain

import android.content.res.Resources
import com.fmontano.weather.android.R
import com.fmontano.weather.android.model.WindModel
import com.fmontano.weather.android.repo.RequestResponse
import com.fmontano.weather.android.repo.WeatherRepository
import com.fmontano.weather.android.ui.model.ForecastUIModel
import com.fmontano.weather.android.ui.model.LocationWeatherUIModel
import com.fmontano.weather.android.ui.model.WeatherUIModel
import com.fmontano.weather.android.utils.DispatcherProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Use case that fetches the weather information and weather forecast for a given location.
 */
class LocationWeatherWithForecastUseCase(
    private val dispatcherProvider: DispatcherProvider,
    private val weatherRepository: WeatherRepository,
    private val resources: Resources,
    private val windSpeedUseCase: WindSpeedUseCase
) {

    private val formatter: SimpleDateFormat = SimpleDateFormat("h aa", Locale.getDefault())

    suspend operator fun invoke(
        lat: Double,
        lon: Double,
        units: String
    ): RequestResponse<WeatherUIModel> =
        withContext(dispatcherProvider.io) {
            val locationDeferred =
                async { weatherRepository.getLocationWeather(lat = lat, lon = lon, units = units) }
            val forecastDeferred =
                async { weatherRepository.getForecast(lat = lat, lon = lon, units = units, itemCount = 16) }

            val location = locationDeferred.await()
            val forecast = forecastDeferred.await()

            val forecastList = if (forecast.isSuccessful) {
                forecast.body()?.list?.map { forecastItem ->
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.timeInMillis = forecastItem.dateTime
                    ForecastUIModel(
                        id = forecastItem.dateTime,
                        iconUrl = "https://openweathermap.org/img/wn/${forecastItem.weather.first().icon}@4x.png",
                        time = formatter.format(Date(forecastItem.dateTime * 1000)),
                        condition = forecastItem.weather.first().description.replaceFirstChar { it.uppercase() },
                        weather = resources.getString(R.string.temperature_with_symbol, forecastItem.main.temp.roundToInt())
                    )
                }
            } else {
                null
            }

            val locationWeather = if (location.isSuccessful) {
                val weatherResponse = location.body()
                val temperature = weatherResponse?.main?.temp?.roundToInt()
                val weatherModel = weatherResponse?.weather?.firstOrNull()
                LocationWeatherUIModel(
                    id = weatherResponse!!.id,
                    name = weatherResponse.name,
                    currentTemp = resources.getString(R.string.temperature_with_symbol, weatherResponse.main.temp.roundToInt()),
                    iconUrl = "https://openweathermap.org/img/wn/${weatherModel?.icon}@4x.png",
                    feelsLike = resources.getString(R.string.feels_like, temperature),
                    currentConditions = weatherModel?.description?.replaceFirstChar { it.uppercase() },
                    minTemp = resources.getString(R.string.temperature_with_symbol, weatherResponse.main.tempMin.roundToInt()),
                    maxTemp = resources.getString(R.string.temperature_with_symbol, weatherResponse.main.tempMax.roundToInt()),
                    windSpeed = windSpeedUseCase(weatherResponse.wind),
                    humidity = "${weatherResponse.main.humidity}%"
                )
            } else {
                null
            }

            if (locationWeather == null) {
                RequestResponse.Error()
            } else {
                RequestResponse.Success(
                    WeatherUIModel(
                        location = locationWeather,
                        forecast = forecastList
                    )
                )
            }
        }
}
