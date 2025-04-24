package com.lib.pay.from.libpfu.repo

import com.lib.pay.from.libpfu.models.CreatePaymentRequestModel
import com.lib.pay.from.libpfu.models.CreatePaymentResponse
import com.lib.pay.from.libpfu.models.SubmitPaymentResponse


interface PaymentRepository {
    suspend fun createTransaction(requestModel: CreatePaymentRequestModel, onCreateSuccess: (CreatePaymentResponse) -> Unit, onCreateFailed: (String) -> Unit)
    suspend fun submitPaymentStatus(title: String, description: String, onPaymentSubmitSuccess: (SubmitPaymentResponse) -> Unit, onPaymentSubmitFailed: (String) -> Unit)
}
