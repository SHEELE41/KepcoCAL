package com.mevius.kepcocal.data.network

import com.mevius.kepcocal.data.network.model.ResultGetCoordinate
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * [GeocoderAPI Interface]
 * 주소-좌표 변환 Kakao REST API 사용을 위한 Interface
 * Http 통신을 위한 헤더 정보 설정, 쿼리 함수 설정 등...
 */
interface GeocodeApiService {
    @GET("v2/local/search/address.json")   // API 상세 경로, format : json
    suspend fun getCoordinate(
        @Query("query") query: String,  // 주소
        @Query("page") display: Int? = null,
        @Query("size") start: Int? = null
    ): Response<ResultGetCoordinate>
}