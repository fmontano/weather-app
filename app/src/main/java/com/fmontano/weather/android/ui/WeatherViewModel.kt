package com.fmontano.weather.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmontano.weather.android.domain.GetLastSavedLocationUseCase
import com.fmontano.weather.android.domain.GetUnitMeasurementSystemUseCase
import com.fmontano.weather.android.domain.LocationWeatherWithForecastUseCase
import com.fmontano.weather.android.domain.SaveSelectedLocationUseCase
import com.fmontano.weather.android.domain.SearchLocationUseCase
import com.fmontano.weather.android.domain.SetUnitMeasurementSystemUseCase
import com.fmontano.weather.android.repo.LocationRepository
import com.fmontano.weather.android.repo.RequestResponse
import com.fmontano.weather.android.ui.model.GeoLocationUIModel
import com.fmontano.weather.android.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val locationWeatherWithForecastUseCase: LocationWeatherWithForecastUseCase,
    private val searchLocationUseCase: SearchLocationUseCase,
    private val saveSelectedLocationUseCase: SaveSelectedLocationUseCase,
    private val getLastSavedLocationUseCase: GetLastSavedLocationUseCase,
    private val getUnitMeasurementSystemUseCase: GetUnitMeasurementSystemUseCase,
    private val setUnitMeasurementSystemUseCase: SetUnitMeasurementSystemUseCase,
    private val locationRepository: LocationRepository
): ViewModel() {

    private val _uiStateFlow = MutableStateFlow(WeatherDetailsUiState())
    val uiState = _uiStateFlow.asStateFlow()

    private fun getWeather(lat: Double, lon: Double, units: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiStateFlow.value = _uiStateFlow.value.copy(
                showLoadingWeatherProgressView = true,
                showEmptyState = false
            )
            val response = locationWeatherWithForecastUseCase(lat, lon, units)
            if (response is RequestResponse.Success) {
                _uiStateFlow.value = _uiStateFlow.value.copy(
                    weather = response.data,
                    showLoadingWeatherProgressView = false,
                    showError = false
                )
            } else {
                _uiStateFlow.value = _uiStateFlow.value.copy(
                    showLoadingWeatherProgressView = false,
                    showError = true
                )
            }
        }
    }

    /**
     * Fetches the weather information from the last location searched by the user
     */
    fun fetchWeatherFromSavedSearch() {
        getLastSavedLocationUseCase()?.let { location ->
            getWeather(
                lat = location.lat,
                lon = location.lon,
                units = getUnitMeasurementSystemUseCase().name.lowercase()
            )
        } ?: run {
            _uiStateFlow.value = _uiStateFlow.value.copy(
                showEmptyState = true
            )
        }
    }

    /**
     * Gets the weather for the user's current location. If the user's current location is not
     * available, we fallback to the last searched location
     */
    fun getWeatherForCurrentLocation() {
        viewModelScope.launch(dispatcherProvider.io) {
            locationRepository.getLocation().collectLatest { currentLocation ->
                currentLocation?.let {
                    getWeather(
                        lat = it.latitude,
                        lon = it.longitude,
                        units = getUnitMeasurementSystemUseCase().name.lowercase()
                    )
                } ?: fetchWeatherFromSavedSearch()
            }
        }
    }

    fun searchLocation(searchQuery: String?) {
        _uiStateFlow.value = _uiStateFlow.value.copy(
            showSearchProgressView = true,
            showSearchEmptyView = false,
        )
        if (searchQuery == null) {
            _uiStateFlow.value = _uiStateFlow.value.copy(
                showSearchProgressView = false,
                searchResults = emptyList(),
                showSearchEmptyView = true,
            )
        } else {
            viewModelScope.launch(dispatcherProvider.io) {
                val searchResults = searchLocationUseCase(searchQuery).orEmpty()
                _uiStateFlow.value = _uiStateFlow.value.copy(
                    showSearchProgressView = false,
                    showSearchEmptyView = searchResults.isEmpty(),
                    searchResults = searchResults
                )
            }
        }
    }

    fun onLocationClicked(location: GeoLocationUIModel) {
        saveSelectedLocationUseCase(location)
        getWeather(
            lat = location.lat,
            lon = location.lon,
            units = getUnitMeasurementSystemUseCase().name.lowercase()
        )
    }

    fun setMeasurementUnits(unitMeasurementSystem: UnitMeasurementSystem) {
        _uiStateFlow.value = _uiStateFlow.value.copy(
            unitMeasurementSystem = unitMeasurementSystem
        )
        setUnitMeasurementSystemUseCase(unitMeasurementSystem)
        getWeatherForCurrentLocation()
    }
}
