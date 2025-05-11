package com.lib.pay.from.libpfu

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class PaymentResultFragment : Fragment() {

    private var onResultCallback: ((String?) -> Unit)? = null

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = result.data?.getStringExtra("response")
        onResultCallback?.invoke(response)
    }

    fun launch(intent: Intent, callback: (String?) -> Unit) {
        this.onResultCallback = callback
        resultLauncher.launch(intent)
    }

    companion object {
        fun getOrCreate(manager: FragmentManager): PaymentResultFragment {
            val tag = "PaymentResultFragment"
            return manager.findFragmentByTag(tag) as? PaymentResultFragment
                ?: PaymentResultFragment().also {
                    manager.beginTransaction().add(it, tag).commitNow()
                }
        }
    }
}
