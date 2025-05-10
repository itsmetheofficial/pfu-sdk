package com.lib.pay.from.libpfu.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class SubmitPaymentResponse(
    @SerializedName("isSuccessful")
    @Expose
    val isSuccessful: Int =0,
    @SerializedName("message")
    @Expose
    val message: String
)

