package com.lib.pay.from.upi

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
import kotlinx.coroutines.launch
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
        paymentManager.setCallbacks(callbacks = this,showBuiltInDialog = true)
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
        Log.d("PayFromUpi", "onCreateSuccess: $response")
    }

    override fun onCreateFailed(error: String) {
        Log.d("PayFromUpi", "onCreateFailed: $error")
    }



    override fun onPaymentSubmitSuccess(response: SubmitPaymentResponse) {
        Log.d("PayFromUpi", "onPaymentSubmitSuccess: $response")
    }

    override fun onPaymentSubmitFailed(error: String) {
        Log.d("PayFromUpi", "onPaymentSubmitFailed: $error")
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