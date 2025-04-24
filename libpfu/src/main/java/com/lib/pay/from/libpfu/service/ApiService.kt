package com.lib.pay.from.libpfu.service


import com.lib.pay.from.libpfu.models.CreatePaymentRequestModel
import com.lib.pay.from.libpfu.models.CreatePaymentResponse
import com.lib.pay.from.libpfu.models.SubmitPaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("transactions/create")
    suspend fun createTransaction(@Body request: CreatePaymentRequestModel): Response<CreatePaymentResponse>

    @POST("submit-payment-status")
    suspend fun submitPaymentStatus(@Query("title") title: String, @Query("description") description: String): Response<SubmitPaymentResponse>
}
