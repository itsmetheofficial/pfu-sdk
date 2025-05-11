package com.lib.pay.from.libpfu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.lib.pay.from.libpfu.callbacks.PaymentCallbacks
import com.lib.pay.from.libpfu.di.NetworkModule
import com.lib.pay.from.libpfu.models.CreatePaymentRequestModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PaymentManagerImp : PaymentManager {

    private var paymentCallbacks: PaymentCallbacks? = null
    private var showDialogs: Boolean = true
    private var activity: ComponentActivity? = null
    private lateinit var resultFragment: PaymentResultFragment
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    override fun initialize(activity: ComponentActivity) {
        NetworkModule.init(activity)
        this.activity = activity
        if (activity is FragmentActivity) {
            resultFragment = PaymentResultFragment.getOrCreate(activity.supportFragmentManager)
        } else {
            activity.setResultLauncher()
        }
    }
    private fun ComponentActivity.setResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val response = data?.getStringExtra("response")
                    //Toast.makeText(this, "RESULT_OK: response ${data?.getStringExtra("response")?:"null"}", Toast.LENGTH_SHORT).show()
                    // Log.d("PaymentManager", "response: $response")
                    if (response != null) {
                        //paymentCallbacks?.onPaymentSuccess(response)
                        lifecycleScope.launch {
                            submitPaymentStatus(title = response, description = response)
                        }
                    } else {
                        //paymentCallbacks?.onPaymentFailed("Payment not completed Response is null")
                    }
                } else {
                    //paymentCallbacks?.onPaymentFailed("Payment not completed")
                }
            }
    }
    override fun initialize(activity: FragmentActivity) {
        NetworkModule.init(activity)
        this.activity = activity
        resultFragment = PaymentResultFragment.getOrCreate(activity.supportFragmentManager)
    }

    override fun setCallbacks(callbacks: PaymentCallbacks, showBuiltInDialog: Boolean) {
        this.paymentCallbacks = callbacks
        this.showDialogs = showBuiltInDialog
    }

    override suspend fun createTransaction(
        bearerToken: String,
        userName: String,
        email: String,
        mobile: String,
        amount: Int,
        redirectUrl: String,
        webhookUrlId: Int
    ) {
        activity?.applicationContext?.let { NetworkModule.setBearerToken(it, bearerToken) }

        NetworkModule.getPaymentRepository().createTransaction(
            requestModel = CreatePaymentRequestModel(
                from = "SDK_DIRECT_INTENT",
                type = "any",
                userName = userName,
                userEmail = email,
                userMobile = mobile,
                amount = amount,
                redirectUrl = redirectUrl,
                webhookUrlId = webhookUrlId,
            ),
            onCreateSuccess = { response ->
                paymentCallbacks?.onCreateSuccess(response)
                activity?.let {
                    openQueryUrl(it, response.data?.queryUrl)
                }
            },
            onCreateFailed = { error ->
                paymentCallbacks?.onCreateFailed(error)
                showDialog("Payment Create Failed!", error)
            }
        )
    }

    private suspend fun submitPaymentStatus(title: String, description: String) {
        NetworkModule.getPaymentRepository().submitPaymentStatus(
            title = title,
            description = description,
            onPaymentSubmitSuccess = { response ->
                paymentCallbacks?.onPaymentSubmitSuccess(response)
                showDialog("Payment Submit Successfully!", response.message)
            },
            onPaymentSubmitFailed = { error ->
                paymentCallbacks?.onPaymentSubmitFailed(error)
                showDialog("Payment Submit Failed!", error)
            }
        )
    }

    private fun openQueryUrl(context: Context, url: String?) {
        if (!url.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            resultFragment.launch(intent) { response ->
                if (response != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        submitPaymentStatus(response, response)
                    }
                }
            }
        }
    }

    fun showDialog(title: String, message: String) {
        activity?.takeIf { showDialogs && !it.isFinishing }?.let {
            val themedContext = ContextThemeWrapper(it, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
            AlertDialog.Builder(themedContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
                .create()
                .show()
        }
    }
}


/*
class PaymentManagerImp  : PaymentManager {
    private var paymentCallbacks: PaymentCallbacks? = null
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var showDialogs: Boolean = true
    private var activity: ComponentActivity?=null
    // Initialize with Bearer Token
    override fun initialize(activity: ComponentActivity) {
        NetworkModule.init(activity)
        this.activity=activity
        activity.setResultLauncher()
    }
    private lateinit var resultFragment: PaymentResultFragment

    fun initializeInsideFragment(activity: FragmentActivity) {
        NetworkModule.init(activity)
        this.activity=activity
        resultFragment = PaymentResultFragment.getOrCreate(activity.supportFragmentManager)
    }

    // Initialize result launcher inside the Activity or Fragment
    private fun ComponentActivity.setResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val response = data?.getStringExtra("response")
                    //Toast.makeText(this, "RESULT_OK: response ${data?.getStringExtra("response")?:"null"}", Toast.LENGTH_SHORT).show()
                   // Log.d("PaymentManager", "response: $response")
                    if (response != null) {
                        //paymentCallbacks?.onPaymentSuccess(response)
                        lifecycleScope.launch {
                            submitPaymentStatus(title = response, description = response)
                        }
                    } else {
                        //paymentCallbacks?.onPaymentFailed("Payment not completed Response is null")
                    }
                } else {
                    //paymentCallbacks?.onPaymentFailed("Payment not completed")
                }
            }
    }

    // Set callback listeners
    override fun setCallbacks(callbacks: PaymentCallbacks,showBuiltInDialog:Boolean) {
        this.paymentCallbacks = callbacks
        this.showDialogs = showBuiltInDialog
    }

    // Example API call function for creating a transaction
    override suspend fun createTransaction(
        bearerToken: String,
        userName: String,
        email: String,
        mobile: String,
        amount: Int,
        redirectUrl: String
    ) {
        // Set the Bearer token in the Network module
        activity?.applicationContext?.let { NetworkModule.setBearerToken(it, bearerToken) }
        // Call the repository to make the API call
        NetworkModule.getPaymentRepository().createTransaction(
            requestModel = CreatePaymentRequestModel(
                from = "SDK_DIRECT_INTENT",
                type = "any",
                userName = userName,
                userEmail = email,
                userMobile = mobile,
                amount = amount,
                redirectUrl = redirectUrl
            ),
            onCreateSuccess = { response ->
                paymentCallbacks?.onCreateSuccess(response)
                activity?.let { openQueryUrl(context = it, url = response.data?.queryUrl) }
            },
            onCreateFailed = { error ->
                paymentCallbacks?.onCreateFailed(error)
                showDialog("Payment Create Failed!",error)
            }
        )
    }

    // Example of another API call for submitting payment status
    private suspend fun submitPaymentStatus(title: String, description: String) {
        NetworkModule.getPaymentRepository().submitPaymentStatus(
            title = title,
            description = description,
            onPaymentSubmitSuccess = { response ->
                paymentCallbacks?.onPaymentSubmitSuccess(response)
                activity?.let {
                    showDialog("Payment Submit Successfully!",response.message)
                }
            },
            onPaymentSubmitFailed = { error ->
                paymentCallbacks?.onPaymentSubmitFailed(error)
                activity?.let {
                    showDialog(title="Payment Submit Failed!", message = error)
                }
            }
        )
    }

    private fun openQueryUrl(context: Context, url: String?) {
       // Toast.makeText(context, "QueryUrl: ${url?:"null"}", Toast.LENGTH_SHORT).show()
        if (!url.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(context.packageManager) != null) {
                resultLauncher?.launch(intent)
            } else {
               // paymentCallbacks?.onPaymentFailed("No application available to handle this request!");
            }
        } else {
            //paymentCallbacks?.onPaymentFailed("Invalid payment query url");
        }

    }
    fun showDialog(title: String,message: String) {
        if (showDialogs && activity!=null &&  !activity!!.isFinishing){
            val themedContext = ContextThemeWrapper(activity, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)

            val builder = AlertDialog.Builder(themedContext)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        }
    }
}
*/

