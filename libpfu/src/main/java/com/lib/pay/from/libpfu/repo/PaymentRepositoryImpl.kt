package com.lib.pay.from.libpfu.repo

import com.lib.pay.from.libpfu.models.CreatePaymentRequestModel
import com.lib.pay.from.libpfu.models.CreatePaymentResponse
import com.lib.pay.from.libpfu.models.SubmitPaymentResponse
import com.lib.pay.from.libpfu.service.ApiService
import javax.inject.Inject


class PaymentRepositoryImpl @Inject constructor(private val apiService: ApiService) : PaymentRepository {

    override suspend fun createTransaction(
        requestModel: CreatePaymentRequestModel,
        onCreateSuccess: (CreatePaymentResponse) -> Unit,
        onCreateFailed: (String) -> Unit
    ) {


        try {
            val response = apiService.createTransaction(request = requestModel)
            if (response.isSuccessful) {
                response.body()?.let {
                    if(it.success){
                        onCreateSuccess(it)
                    }else{
                        onCreateFailed(it.message)
                    }
                }?:run {
                    onCreateFailed("Response body not received")
                }
            } else {
                onCreateFailed("Failed to create transaction")
            }
        } catch (e: Exception) {
            onCreateFailed("Error: ${e.message}")
        }
    }

    override suspend fun submitPaymentStatus(
        title: String,
        description: String,
        onPaymentSubmitSuccess: (SubmitPaymentResponse) -> Unit,
        onPaymentSubmitFailed: (String) -> Unit
    ) {
        try {
            val response = apiService.submitPaymentStatus(title, description)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.message.contains("RES 200")){
                        onPaymentSubmitSuccess(it)
                    }else{
                        onPaymentSubmitFailed(it.message)
                    }
                }?:run {
                    onPaymentSubmitFailed("Response body not received")
                }
            } else {
                onPaymentSubmitFailed("Failed to submit payment status")
            }
        } catch (e: Exception) {
            onPaymentSubmitFailed("Error: ${e.message}")
        }
    }
}
