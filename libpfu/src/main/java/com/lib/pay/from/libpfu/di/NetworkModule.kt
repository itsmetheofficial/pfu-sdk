package com.lib.pay.from.libpfu.di

import android.content.Context
import com.lib.pay.from.libpfu.PaymentManager
import com.lib.pay.from.libpfu.PaymentManagerImp
import com.lib.pay.from.libpfu.repo.PaymentRepository
import com.lib.pay.from.libpfu.repo.PaymentRepositoryImpl
import com.lib.pay.from.libpfu.service.ApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton





@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @PaymentOkHttp
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val token = getBearerToken(context)
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    @PaymentRetrofit
    fun provideRetrofit(@PaymentOkHttp okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://payfromupi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(@PaymentRetrofit retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(apiService: ApiService): PaymentRepository {
        return PaymentRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun providePaymentManager(repo: PaymentRepository): PaymentManager {
        return PaymentManagerImp(repo)
    }

    private fun getBearerToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("bearer_token", "") ?: ""
    }

    fun setBearerToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("bearer_token", token)
            apply()
        }
    }
}

