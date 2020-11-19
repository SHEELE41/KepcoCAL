package com.mevius.kepcocal.data.network

import com.mevius.kepcocal.data.network.model.ResultGetCoordinate
import retrofit2.Response
import javax.inject.Inject

class GeocodeApiHelperImpl @Inject constructor(private val geocodeApiService: GeocodeApiService): GeocodeApiHelper {
    override suspend fun getCoordinate(query: String): Response<ResultGetCoordinate> = geocodeApiService.getCoordinate(query)
}