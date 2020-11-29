package com.mevius.kepcocal.di

import com.mevius.kepcocal.BuildConfig
import com.mevius.kepcocal.data.network.GeocodeApiHelper
import com.mevius.kepcocal.data.network.GeocodeApiHelperImpl
import com.mevius.kepcocal.data.network.GeocodeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {
    @BaseUrl
    @Provides
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @AuthorizationKey
    @Provides
    fun provideAuthorizationKey(): String = BuildConfig.AUTHORIZATION_KEY

    @Provides
    @Singleton
    fun provideOkHttpClient(@AuthorizationKey AUTHORIZATION_KEY: String): OkHttpClient =
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("Authorization", AUTHORIZATION_KEY)
                    .build()
                return@Interceptor it.proceed(request)
            }

            OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
        } else OkHttpClient
            .Builder()
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, @BaseUrl BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): GeocodeApiService =
        retrofit.create(GeocodeApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(geocodeApiHelper: GeocodeApiHelperImpl): GeocodeApiHelper =
        geocodeApiHelper
}
