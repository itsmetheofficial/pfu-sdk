package com.lib.pay.from.libpfu

import android.content.Context
import androidx.activity.ComponentActivity
import com.lib.pay.from.libpfu.callbacks.PaymentCallbacks

interface PaymentManager {
    fun initialize(activity: ComponentActivity)
    fun setCallbacks(callbacks: PaymentCallbacks,showBuiltInDialog:Boolean=true)
    suspend fun createTransaction(context: Context, bearerToken: String,userName:String,email:String,mobile:String,amount:Int,redirectUrl:String)
}
