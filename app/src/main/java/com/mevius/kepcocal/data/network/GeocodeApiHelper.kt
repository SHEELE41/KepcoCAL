package com.mevius.kepcocal.data.network

import com.mevius.kepcocal.data.network.model.ResultGetCoordinate
import retrofit2.Response

interface GeocodeApiHelper {
    suspend fun getCoordinate(query: String): Response<ResultGetCoordinate>
}