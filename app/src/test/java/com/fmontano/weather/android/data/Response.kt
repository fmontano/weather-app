package com.fmontano.weather.android.data

import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

fun <T> responseError(errorCode: Int = 400) = Response.error<T>(errorCode, byteArrayOf().toResponseBody())

