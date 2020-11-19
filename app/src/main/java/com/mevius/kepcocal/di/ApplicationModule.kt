package com.mevius.kepcocal.di

import com.mevius.kepcocal.BuildConfig
import com.mevius.kepcocal.data.network.GeocoderApiService
import com.mevius.kepcocal.data.network.GeocoderApiHelper
import com.mevius.kepcocal.data.network.GeocoderApiHelperImpl
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
class ApplicationModule {
    @Provides
    fun provideAuthorizationKey() = BuildConfig.AUTHORIZATION_KEY

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(AUTHORIZATION_KEY: String): OkHttpClient = if (BuildConfig.DEBUG) {
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
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(GeocoderApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(geocoderApiHelper : GeocoderApiHelperImpl): GeocoderApiHelper = geocoderApiHelper
}
