package com.fmontano.weather.android.ui

enum class UnitMeasurementSystem(val speedUnit: String) {
    IMPERIAL("mph"),
    METRIC("kph");

    companion object {
        fun parseString(
            unitName: String?,
            defaultValue: UnitMeasurementSystem = IMPERIAL
        ): UnitMeasurementSystem {
            return values().find { it.name.lowercase() == unitName?.lowercase() } ?: defaultValue
        }
    }
}
