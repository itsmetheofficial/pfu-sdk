package com.lib.pay.from.upi

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.lib.pay.from.libpfu.PaymentManager
import com.lib.pay.from.libpfu.callbacks.PaymentCallbacks
import com.lib.pay.from.libpfu.models.CreatePaymentResponse
import com.lib.pay.from.libpfu.models.SubmitPaymentResponse
import com.lib.pay.from.upi.ui.theme.LibPayFromUpiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Observable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentCallbacks {
    @Inject
    lateinit var paymentManager: PaymentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize the PaymentManager with Bearer Token
        paymentManager.initialize(this)
        // Set the callback listeners
        paymentManager.setCallbacks(this)
        // Trigger the API call (for example, create a transaction)
        /* lifecycleScope.launch {
             paymentManager.createTransaction(applicationContext, bearerToken = "1|gI3EA9fdWDaxV6vAox36pENIxCkUNnJFjTvxvrUCbb6b3d01",
                 userName = "test", email = "user@gmail.com", mobile = "8383838383", amount = 100, redirectUrl = "https://google.com")
         }*/
        setContent {
            LibPayFromUpiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InputScreen(
                        modifier = Modifier.padding(innerPadding),
                        onPayClick = { bearerToken, username, email, phone, amount, redirectUrl ->
                            lifecycleScope.launch {
                                paymentManager.createTransaction(
                                    applicationContext,
                                    bearerToken=bearerToken,
                                    username,
                                    email,
                                    phone,
                                    amount.toInt(),
                                    redirectUrl
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onCreateSuccess(response: CreatePaymentResponse) {
        Toast.makeText(this, "onCreateSuccess", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "onCreateSuccess: $response")
    }

    override fun onCreateFailed(error: String) {
        Toast.makeText(this, "onCreateFailed: $error", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "onCreateFailed: $error")
    }

    override fun onPaymentSuccess(response: String) {
        Toast.makeText(this, "onPaymentSuccess", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "onPaymentSuccess: $response")
        /*val builder = AlertDialog.Builder(this)

        // Set the title of the dialog
        builder.setTitle("Payment Successful")

        // Set the message of the dialog, displaying the response
        builder.setMessage("Response: ${response}")

        // Add a positive button with an action (e.g., dismiss the dialog)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss() // Dismiss the dialog when the button is clicked
            // You can add further actions here if needed
        }

        // Prevent the user from dismissing the dialog by tapping outside
        builder.setCancelable(false)

        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()*/
    }

    override fun onPaymentFailed(error: String) {
        Toast.makeText(this, "onPaymentFailed: $error", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "onPaymentFailed: $error")
    }

    override fun onPaymentSubmitSuccess(response: SubmitPaymentResponse) {
        Toast.makeText(this, "onPaymentSubmitSuccess", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "onPaymentSubmitSuccess: $response")
    }

    override fun onPaymentSubmitFailed(error: String) {
        Toast.makeText(this, "onPaymentSubmitFailed: $error", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "onPaymentSubmitFailed: $error")
    }
}

@Composable
fun InputScreen(
    modifier: Modifier,
    onPayClick: (String, String, String, String, String, String) -> Unit
) {
    // State variables to store input values
    var bearerToken by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var redirectUrl by remember { mutableStateOf("") }

    // Column layout for the inputs
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Bearer Token Input
        TextField(
            value = bearerToken,
            onValueChange = { bearerToken = it },
            label = { Text("Bearer Token") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Username Input
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Email Input
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Phone Input
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Amount Input
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Redirect URL Input
        TextField(
            value = redirectUrl,
            onValueChange = { redirectUrl = it },
            label = { Text("Redirect URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Submit Button
        Button(
            onClick = {
                // Handle form submission logic here
                if (bearerToken.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && amount.isNotEmpty() && redirectUrl.isNotEmpty()) {
                    // Process the form data
                    println(
                        "Form Submitted with the following details: \n" +
                                "Bearer Token: $bearerToken\n" +
                                "Username: $username\n" +
                                "Email: $email\n" +
                                "Phone: $phone\n" +
                                "Amount: $amount\n" +
                                "Redirect URL: $redirectUrl"
                    )
                    onPayClick(bearerToken, username, email, phone, amount, redirectUrl)
                } else {
                    // Handle validation error (show a message, etc.)
                    println("Please fill all the fields.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Pay")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LibPayFromUpiTheme {
        InputScreen(modifier = Modifier, onPayClick = { bearerToken, username, email, phone, amount, redirectUrl ->})
    }
}