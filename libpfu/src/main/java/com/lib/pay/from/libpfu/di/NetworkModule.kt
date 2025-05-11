package com.lib.pay.from.libpfu.di

import android.content.Context
import com.lib.pay.from.libpfu.repo.PaymentRepository
import com.lib.pay.from.libpfu.repo.PaymentRepositoryImpl
import com.lib.pay.from.libpfu.service.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private var apiService: ApiService? = null
    private var paymentRepository: PaymentRepository? = null


    fun init(context: Context) {
        val okHttpClient = provideOkHttpClient(context)
        val retrofit = provideRetrofit(okHttpClient)
        apiService = retrofit.create(ApiService::class.java)
        paymentRepository = PaymentRepositoryImpl(apiService!!)

    }

    fun getApiService(): ApiService {
        return apiService ?: throw IllegalStateException("NetworkModule not initialized")
    }

    fun getPaymentRepository(): PaymentRepository {
        return paymentRepository ?: throw IllegalStateException("NetworkModule not initialized")
    }


    private fun provideOkHttpClient(context: Context): OkHttpClient {
        val interceptor = Interceptor { chain ->
            // Fetch the latest token every time a request is made
            val token = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                .getString("bearer_token", "") ?: ""

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

/*    private fun provideOkHttpClient(context: Context): OkHttpClient {
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
    }*/

    private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://payfromupi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
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



/*@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkConfigModule {
    @Binds
    abstract fun bindPaymentManagerProvider(impl: PaymentManagerImp): PaymentManager

}*/
