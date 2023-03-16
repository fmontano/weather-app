package com.fmontano.weather.android.domain

import android.content.res.Resources
import com.fmontano.weather.android.data.responseError
import com.fmontano.weather.android.data.successfulForecastResponse
import com.fmontano.weather.android.data.successfulWeatherResponse
import com.fmontano.weather.android.data.validLocationUIModel
import com.fmontano.weather.android.repo.WeatherRepository
import com.fmontano.weather.android.repo.RequestResponse
import com.fmontano.weather.android.utils.TestDispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocationWeatherWithForecastUseCaseTest {

    private lateinit var subject: LocationWeatherWithForecastUseCase
    private val dispatcherProvider = TestDispatcherProvider()

    @RelaxedMockK
    lateinit var weatherRepository: WeatherRepository

    @RelaxedMockK
    lateinit var getUnitMeasurementSystemUseCase: GetUnitMeasurementSystemUseCase

    @RelaxedMockK
    lateinit var resources: Resources

    init {
        MockKAnnotations.init(this)
    }

    @Before
    fun setupTest() {
        subject = LocationWeatherWithForecastUseCase(
            dispatcherProvider = dispatcherProvider,
            weatherRepository = weatherRepository,
            getUnitMeasurementSystemUseCase = getUnitMeasurementSystemUseCase,
            resources = resources
        )
    }

    @Test
    fun `Returns RequestResponse#Error when weather call fails`() = runTest {
        coEvery { weatherRepository.getLocationWeather(any(), any(), any()) } returns responseError()

        val response = subject.invoke(validLocationUIModel.lat, validLocationUIModel.lon, "")

        Assert.assertTrue(response is RequestResponse.Error)
    }

    @Test
    fun `Returns RequestResponse#Success when weather call succeeds and forecast fails`() = runTest {
        coEvery { weatherRepository.getLocationWeather(any(), any(), any()) } returns Response.success(
            successfulWeatherResponse
        )
        coEvery { weatherRepository.getForecast(any(), any(), any(), any()) } returns responseError()

        val response = subject.invoke(validLocationUIModel.lat, validLocationUIModel.lon, "")

        Assert.assertTrue(response is RequestResponse.Success)
        with ((response as RequestResponse.Success).data) {
            Assert.assertNull(forecast)
            Assert.assertEquals(successfulWeatherResponse.id, this.location.id)
            // TODO I'd assert all other fields are mapped correctly given more time
        }
    }

    @Test
    fun `Returns RequestResponse#Success with weather and forecast`() = runTest {
        coEvery { weatherRepository.getLocationWeather(any(), any(), any()) } returns Response.success(
            successfulWeatherResponse
        )
        coEvery { weatherRepository.getForecast(any(), any(), any(), any()) } returns Response.success(
            successfulForecastResponse
        )
        val response = subject.invoke(validLocationUIModel.lat, validLocationUIModel.lon, "")

        Assert.assertTrue(response is RequestResponse.Success)
        with ((response as RequestResponse.Success).data) {
            Assert.assertNotNull(forecast)
            Assert.assertEquals(1, forecast?.size)
            Assert.assertEquals(successfulWeatherResponse.id, this.location.id)
            // TODO I'd assert all other fields are mapped correctly given more time
        }
    }
}
