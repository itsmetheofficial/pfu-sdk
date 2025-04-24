package com.lib.pay.from.libpfu.models


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class CreatePaymentRequestModel(
    @SerializedName("from")
    @Expose
    val from: String,
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("user_name")
    @Expose
    val userName: String,
    @SerializedName("user_email")
    @Expose
    val userEmail: String,
    @SerializedName("user_mobile")
    @Expose
    val userMobile: String,
    @SerializedName("amount")
    @Expose
    val amount: Int,
    @SerializedName("redirect_url")
    @Expose
    val redirectUrl: String,
    @SerializedName("webhook_url_id")
    @Expose
    val webhookUrlId: Int = 1
)