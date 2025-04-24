# 🔁 PayFromUPI

**PayFromUPI** is a simple and lightweight Android Jetpack Compose library that allows seamless integration of UPI-based payment flows with just a few lines of code.

---

## 📦 Installation

### Step 1: Add JitPack to your `settings.gradle.kts`

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // ✅ Required for this library
    }
}
```

### Step 2: Add the library dependency in `build.gradle.kts`

```kotlin
dependencies {
    implementation("com.github.itsmetheofficial:pfu-sdk:1.0.0")
}
```

---

## 🧠 Prerequisites

- Your project must use **Hilt** for dependency injection.
- Annotate your `Application` class with `@HiltAndroidApp`.
- Annotate your `Activity` or `Fragment` with `@AndroidEntryPoint`.

---

## 🚀 How to Use

### Step 1: Inject and initialize `PaymentManager`

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentCallbacks {

    @Inject
    lateinit var paymentManager: PaymentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize PaymentManager
        paymentManager.initialize(this)

        // Set the callbacks
        paymentManager.setCallbacks(this)
```

---

### Step 2: Create a transaction

```kotlin
lifecycleScope.launch {
    paymentManager.createTransaction(
        context = applicationContext,
        bearerToken = "YOUR_API_TOKEN",
        userName = "John Doe",
        email = "john@example.com",
        mobile = "9876543210",
        amount = 100,
        redirectUrl = "https://yourredirect.com"
    )
}
```

---

### 🖼 Optional: Compose UI Integration

```kotlin
InputScreen(
    onPayClick = { bearerToken, username, email, phone, amount, redirectUrl ->
        lifecycleScope.launch {
            paymentManager.createTransaction(
                applicationContext,
                bearerToken = bearerToken,
                username = username,
                email = email,
                mobile = phone,
                amount = amount.toInt(),
                redirectUrl = redirectUrl
            )
        }
    }
)
```

---

## 🔁 Callback Implementation

Implement `PaymentCallbacks` in your Activity or Fragment:

```kotlin
override fun onCreateSuccess(response: CreatePaymentResponse) {
    Log.d("PayFromUPI", "Create Success: $response")
}

override fun onCreateFailed(error: String) {
    Log.e("PayFromUPI", "Create Failed: $error")
}

override fun onPaymentSuccess(response: String) {
    Log.d("PayFromUPI", "Payment Success: $response")
}

override fun onPaymentFailed(error: String) {
    Log.e("PayFromUPI", "Payment Failed: $error")
}

override fun onPaymentSubmitSuccess(response: SubmitPaymentResponse) {
    Log.d("PayFromUPI", "Submit Success: $response")
}

override fun onPaymentSubmitFailed(error: String) {
    Log.e("PayFromUPI", "Submit Failed: $error")
}
```

---

## 💡 Pro Tips

- Make sure the backend provides a **valid Bearer Token**.
- Use `redirectUrl` for post-payment redirection.
- Handle callback responses to show appropriate user messages or UI states.

---

## 📃 License

This project is licensed under the MIT License.

---

## ✨ Developed by

[itsmetheofficial](https://github.com/itsmetheofficial) — Software Development services