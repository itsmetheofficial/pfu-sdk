package com.lib.pay.from.libpfu.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreatePaymentResponse(
    @SerializedName("success")
    @Expose
    val success: Boolean = false,
    @SerializedName("message")
    @Expose
    val message: String="",
    @SerializedName("data")
    @Expose
    val data: Data?=null,
) {
    data class Data(
        @SerializedName("paymentLink")
        @Expose
        val paymentLink: String="",
        @SerializedName("queryUrl")
        @Expose
        val queryUrl: String="",
        @SerializedName("txnId")
        @Expose
        val txnId: String="",
        @SerializedName("qyeryUrl")
        @Expose
        val qyeryUrl: String="",
    )
}


