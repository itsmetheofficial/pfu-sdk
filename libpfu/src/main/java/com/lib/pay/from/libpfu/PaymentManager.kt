package com.lib.pay.from.libpfu

import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import com.lib.pay.from.libpfu.callbacks.PaymentCallbacks

interface PaymentManager {
    fun initialize(activity: ComponentActivity)
    fun initialize(activity: FragmentActivity)
    fun setCallbacks(callbacks: PaymentCallbacks,showBuiltInDialog:Boolean=false)
    suspend fun createTransaction(bearerToken: String,userName:String,email:String,mobile:String,amount:Int,redirectUrl:String="",webhookUrlId:Int=1)
}
