package com.fmontano.weather.android.domain

import com.fmontano.weather.android.model.WindModel
import kotlin.math.roundToInt

class WindSpeedUseCase(
    val getUnitMeasurementSystemUseCase: GetUnitMeasurementSystemUseCase
) {

    private val directions = listOf("↑ N", "↗ NE", "→ E", "↘ SE", "↓ S", "↙ SW", "← W", "↖ NW")

    operator fun invoke(windModel: WindModel): String {
        val direction = directions[(windModel.degrees / 45f).roundToInt() % 8]
        val speedUnits = getUnitMeasurementSystemUseCase().speedUnit

        return "$direction ${windModel.speed} $speedUnits"
    }
}