package com.fmontano.weather.android.network

import com.fmontano.weather.android.model.GeolocationModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeolocationService {

    @GET("geo/1.0/direct")
    suspend fun getDirectGeolocation(
        @Query("q") searchQuery: String,
        @Query("limit") limit: Int = 25,
        @Query("appid") apiKey: String
    ): Response<List<GeolocationModel>>
}
