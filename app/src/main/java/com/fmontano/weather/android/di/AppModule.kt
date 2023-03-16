package com.fmontano.weather.android.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.fmontano.weather.android.R
import com.fmontano.weather.android.domain.GetLastSavedLocationUseCase
import com.fmontano.weather.android.domain.GetUnitMeasurementSystemUseCase
import com.fmontano.weather.android.domain.LocationWeatherWithForecastUseCase
import com.fmontano.weather.android.domain.SaveSelectedLocationUseCase
import com.fmontano.weather.android.domain.SearchLocationUseCase
import com.fmontano.weather.android.domain.SetUnitMeasurementSystemUseCase
import com.fmontano.weather.android.domain.WindSpeedUseCase
import com.fmontano.weather.android.network.GeolocationService
import com.fmontano.weather.android.network.WeatherService
import com.fmontano.weather.android.repo.GeolocationRepository
import com.fmontano.weather.android.repo.LocationRepository
import com.fmontano.weather.android.repo.SharedPreferencesRepository
import com.fmontano.weather.android.repo.WeatherRepository
import com.fmontano.weather.android.utils.DefaultDispatcherProvider
import com.fmontano.weather.android.utils.DispatcherProvider
import com.fmontano.weather.android.utils.WeatherLocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class ApiKey

@Qualifier
annotation class LocationSharedPreferences

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        // TODO in a production app I'd move this endpoint to an environment variable so we can
        //   change the environment in CI based on the stage we are building for (dev, stg, prod, etc)
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesWeatherService(
        retrofit: Retrofit
    ) = retrofit.create<WeatherService>()

    @Singleton
    @Provides
    fun providesGeolocationService(
        retrofit: Retrofit
    ) = retrofit.create<GeolocationService>()

    @Singleton
    @Provides
    @ApiKey
    // TODO I'd never ever hardcode an app key here! Should be in an env variable or a
    //   secret management tool
    fun providesApiKey(): String = "4b37d5b1d788c5a173dd6fad75e90c20"

    @Singleton
    @Provides
    @LocationSharedPreferences
    fun providesLocationSharedPreferences(application: Application) =
        application.resources.getString(R.string.app_name)

    @Singleton
    @Provides
    fun providesSharedPreferences(
        application: Application,
        @LocationSharedPreferences prefsName: String
    ) = application.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun providesDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    // Repositories
    @Singleton
    @Provides
    fun providesSharedPreferencesRepository(
        sharedPreferences: SharedPreferences
    ) = SharedPreferencesRepository(sharedPreferences)

    @Singleton
    @Provides
    fun providesWeatherRepository(
        weatherService: WeatherService,
        @ApiKey apiKey: String
    ) = WeatherRepository(
        weatherService = weatherService,
        apiKey = apiKey
    )

    @Singleton
    @Provides
    fun providesGeolocationRepository(
        geolocationService: GeolocationService,
        @ApiKey apiKey: String
    ) = GeolocationRepository(
        geolocationService = geolocationService,
        apiKey = apiKey
    )

    @Singleton
    @Provides
    fun providesLocationRepository(
        weatherLocationManager: WeatherLocationManager
    ) = LocationRepository(weatherLocationManager)

    @Singleton
    @Provides
    fun providesWeatherLocationManager(
        application: Application
    ) = WeatherLocationManager(application)

    // Use cases
    @Singleton
    @Provides
    fun providesSaveSelectedLocationUseCase(
        sharedPreferencesRepository: SharedPreferencesRepository
    ) = SaveSelectedLocationUseCase(sharedPreferencesRepository)

    @Singleton
    @Provides
    fun providesGetLastSavedLocationUseCase(
        sharedPreferencesRepository: SharedPreferencesRepository
    ) = GetLastSavedLocationUseCase(sharedPreferencesRepository)

    @Singleton
    @Provides
    fun providesGetUnitMeasurementSystemUseCase(
        sharedPreferencesRepository: SharedPreferencesRepository
    ) = GetUnitMeasurementSystemUseCase(sharedPreferencesRepository)

    @Singleton
    @Provides
    fun providesSetUnitMeasurementSystemUseCase(
        sharedPreferencesRepository: SharedPreferencesRepository
    ) = SetUnitMeasurementSystemUseCase(sharedPreferencesRepository)

    @Singleton
    @Provides
    fun providesLocationWeatherWithForecastUseCase(
        dispatcherProvider: DispatcherProvider,
        weatherRepository: WeatherRepository,
        application: Application,
        windSpeedUseCase: WindSpeedUseCase
    ) = LocationWeatherWithForecastUseCase(
        dispatcherProvider = dispatcherProvider,
        weatherRepository = weatherRepository,
        resources = application.resources,
        windSpeedUseCase = windSpeedUseCase
    )

    @Singleton
    @Provides
    fun providesWinSpeedUseCase(
        getUnitMeasurementSystemUseCase: GetUnitMeasurementSystemUseCase
    ) = WindSpeedUseCase(getUnitMeasurementSystemUseCase)

    @Singleton
    @Provides
    fun providesSearchLocationUseCase(
        dispatcherProvider: DispatcherProvider,
        geolocationRepository: GeolocationRepository
    ) = SearchLocationUseCase(
        dispatcherProvider = dispatcherProvider,
        geolocationRepository = geolocationRepository
    )
}
