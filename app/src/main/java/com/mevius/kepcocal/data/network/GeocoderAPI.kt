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
interface GeocoderAPI {
    @GET("v2/local/search/address.json")   // API 상세 경로, format : json
    suspend fun getCoordinate(
        @Query("query") query: String,
        @Query("page") display: Int? = null,
        @Query("size") start: Int? = null
    ): Response<ResultGetCoordinate>

    /**
     * [GeocoderAPI Interface companion object]
     * 모든 API 요청에 공통적인 static 필드, 메소드 설정을 위한 companion object
     * GeocoderAPI.create()를 이용하여 설정이 다 들어간 객체를 생성할 수 있음.
     */
    companion object {
        // Header에 들어갈 정보 설정
        private const val BASE_URL_KAKAO_API = "https://dapi.kakao.com/"    // API 기본 경로
        private const val AUTHORIZATION_KEY = "KakaoAK 7f560ef33db63b7dec4f618dbd696f67"    // Auth Key

        fun create(): GeocoderAPI {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("Authorization", AUTHORIZATION_KEY)
                    .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL_KAKAO_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeocoderAPI::class.java)
        }
    }
}