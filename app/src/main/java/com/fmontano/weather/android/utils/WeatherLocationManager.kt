package com.fmontano.weather.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Performs location requests via [FusedLocationProviderClient] and reports the results backs in a Flow.
 */
class WeatherLocationManager(
    context: Context
) {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    private var _lastKnownLocationFlow = callbackFlow {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            trySend(location)
        }.addOnFailureListener {
            trySend(null)
        }

        awaitClose { }
    }

    fun locationFlow(): Flow<Location?> {
        return _lastKnownLocationFlow
    }
}
