package com.lib.pay.from.libpfu.callbacks

import com.lib.pay.from.libpfu.models.CreatePaymentResponse
import com.lib.pay.from.libpfu.models.SubmitPaymentResponse

interface PaymentCallbacks {
    fun onCreateSuccess(response: CreatePaymentResponse)
    fun onCreateFailed(error: String)



    fun onPaymentSubmitSuccess(response: SubmitPaymentResponse)
    fun onPaymentSubmitFailed(error: String)
}
