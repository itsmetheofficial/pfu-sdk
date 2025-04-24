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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val token = getBearerToken(context)  // Retrieve token from shared preferences or wherever it's stored
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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://payfromupi.com/api/")  // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
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


    // Function to get the Bearer token, you can change this to your token retrieval logic
    private fun getBearerToken(context: Context): String {
        // You can use SharedPreferences, secure storage, etc. to retrieve the token
        // Here is an example with SharedPreferences:
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("bearer_token", "") ?: ""
    }
    fun setBearerToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("bearer_token", token)
            apply() // or commit()
        }
    }

}


/*@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkConfigModule {
    @Binds
    abstract fun bindPaymentManagerProvider(impl: PaymentManagerImp): PaymentManager

}*/
