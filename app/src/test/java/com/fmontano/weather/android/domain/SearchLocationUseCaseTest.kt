package com.fmontano.weather.android.domain

import com.fmontano.weather.android.data.responseError
import com.fmontano.weather.android.data.validLocationModel
import com.fmontano.weather.android.repo.GeolocationRepository
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
internal class SearchLocationUseCaseTest {

    private lateinit var subject: SearchLocationUseCase
    private val dispatcherProvider = TestDispatcherProvider()

    @RelaxedMockK
    lateinit var geolocationRepository: GeolocationRepository

    init {
        MockKAnnotations.init(this)
    }

    @Before
    fun setupTest() {
        subject = SearchLocationUseCase(
            dispatcherProvider = dispatcherProvider,
            geolocationRepository = geolocationRepository
        )
    }

    @Test
    fun `Search returns null when there is an error`() = runTest {
        coEvery { geolocationRepository.getGeolocation(any()) } returns responseError()

        val response = subject.invoke("")

        Assert.assertNull(response)
    }

    @Test
    fun `Search returns a valid location`() = runTest {
        val searchQuery = "us"
        coEvery { geolocationRepository.getGeolocation(any()) } returns Response.success(
            listOf(validLocationModel)
        )

        val response = subject.invoke(searchQuery)

        Assert.assertNotNull(response)
        Assert.assertEquals(1, response?.size)
        with (response?.first()!!) {
            Assert.assertTrue(validLocationModel.lat == lat)
            Assert.assertTrue(validLocationModel.lon == lon)
            Assert.assertEquals(
                "${validLocationModel.name}, ${validLocationModel.state}, ${validLocationModel.country}",
                displayName
            )
        }
    }
}
