package com.fmontano.weather.android.ui

import com.fmontano.weather.android.data.validLocationUIModel
import com.fmontano.weather.android.data.weatherWithForecast
import com.fmontano.weather.android.domain.GetLastSavedLocationUseCase
import com.fmontano.weather.android.domain.GetUnitMeasurementSystemUseCase
import com.fmontano.weather.android.domain.LocationWeatherWithForecastUseCase
import com.fmontano.weather.android.domain.SaveSelectedLocationUseCase
import com.fmontano.weather.android.domain.SearchLocationUseCase
import com.fmontano.weather.android.domain.SetUnitMeasurementSystemUseCase
import com.fmontano.weather.android.repo.LocationRepository
import com.fmontano.weather.android.repo.RequestResponse
import com.fmontano.weather.android.ui.model.GeoLocationUIModel
import com.fmontano.weather.android.utils.TestDispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class WeatherViewModelTest {

    private lateinit var subject: WeatherViewModel

    @RelaxedMockK
    lateinit var locationWeatherWithForecastUseCase: LocationWeatherWithForecastUseCase
    @RelaxedMockK
    lateinit var searchLocationUseCase: SearchLocationUseCase
    @RelaxedMockK
    lateinit var saveSelectedLocationUseCase: SaveSelectedLocationUseCase
    @RelaxedMockK
    lateinit var getLastSavedLocationUseCase: GetLastSavedLocationUseCase
    @RelaxedMockK
    lateinit var getUnitMeasurementSystemUseCase: GetUnitMeasurementSystemUseCase
    @RelaxedMockK
    lateinit var setUnitMeasurementSystemUseCase: SetUnitMeasurementSystemUseCase
    @RelaxedMockK
    lateinit var locationRepository: LocationRepository

    private val dispatcherProvider = TestDispatcherProvider()

    init {
        MockKAnnotations.init(this)
    }

    @Before
    fun setupTest() {
        subject = WeatherViewModel(
            dispatcherProvider = dispatcherProvider,
            locationWeatherWithForecastUseCase = locationWeatherWithForecastUseCase,
            searchLocationUseCase = searchLocationUseCase,
            saveSelectedLocationUseCase = saveSelectedLocationUseCase,
            getLastSavedLocationUseCase = getLastSavedLocationUseCase,
            getUnitMeasurementSystemUseCase = getUnitMeasurementSystemUseCase,
            setUnitMeasurementSystemUseCase = setUnitMeasurementSystemUseCase,
            locationRepository = locationRepository
        )
    }

    @Test
    fun `fetching weather shows empty state when there is no saved search`() = runTest {
        coEvery { getLastSavedLocationUseCase() } returns null

        subject.fetchWeatherFromSavedSearch()

        Assert.assertTrue(subject.uiState.value.showEmptyState)
    }

    @Test
    fun `fetching weather shows loading indicator while fetching`() = runTest {
        coEvery { getLastSavedLocationUseCase() } returns null

        subject.fetchWeatherFromSavedSearch()

        Assert.assertTrue(subject.uiState.value.showLoadingWeatherProgressView)
    }

    @Test
    fun `fetching weather shows error message when fails`() = runTest {
        coEvery { getLastSavedLocationUseCase() } returns validLocationUIModel
        coEvery {
            locationWeatherWithForecastUseCase(any(), any(), any())
        } returns RequestResponse.Error()

        subject.fetchWeatherFromSavedSearch()

        with (subject.uiState.value) {
            Assert.assertFalse(showLoadingWeatherProgressView)
            Assert.assertTrue(showError)
        }
    }

    @Test
    fun `fetching weather shows data when succeeds`() = runTest {
        coEvery { getLastSavedLocationUseCase() } returns validLocationUIModel
        coEvery {
            locationWeatherWithForecastUseCase(any(), any(), any())
        } returns RequestResponse.Success(weatherWithForecast)

        subject.fetchWeatherFromSavedSearch()

        with (subject.uiState.value) {
            Assert.assertFalse(showLoadingWeatherProgressView)
            Assert.assertFalse(showEmptyState)
            Assert.assertFalse(showError)
            Assert.assertEquals(weather, weatherWithForecast)
        }
    }

    @Test
    fun `Fallback to latest search when cannot get user current location`() = runTest {
        coEvery { locationRepository.getLocation() } returns flowOf(null)

        subject.getWeatherForCurrentLocation()

        coVerify {
            getLastSavedLocationUseCase()
        }
    }

    @Test
    fun `Shows empty view when location search query is null`() = runTest {
        subject.searchLocation(null)

        with (subject.uiState.value) {
            Assert.assertFalse(showSearchProgressView)
            Assert.assertTrue(showSearchEmptyView)
            Assert.assertEquals(emptyList<GeoLocationUIModel>(), searchResults)
        }
    }

    @Test
    fun `Searches for location when search query is valid`() = runTest {
        val searchQuery = "a query"

        subject.searchLocation(searchQuery)

        coVerify { searchLocationUseCase(searchQuery) }
    }

    @Test
    fun `Performs weather search when user taps on a location`() = runTest {
        coEvery { getUnitMeasurementSystemUseCase() } returns UnitMeasurementSystem.IMPERIAL

        subject.onLocationClicked(validLocationUIModel)

        coVerify { saveSelectedLocationUseCase(validLocationUIModel) }
        coVerify { locationWeatherWithForecastUseCase(validLocationUIModel.lat, validLocationUIModel.lon,
            UnitMeasurementSystem.IMPERIAL.name.lowercase()
        ) }
    }

    @Test
    fun `Saves selected units of measurement and fetches weather info`() = runTest {
        val units = UnitMeasurementSystem.METRIC

        subject.setMeasurementUnits(units)

        coVerify { setUnitMeasurementSystemUseCase(units) }
        coVerify { locationRepository.getLocation() }
        Assert.assertEquals(subject.uiState.value.unitMeasurementSystem, units)
    }
}
