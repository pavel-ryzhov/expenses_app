package com.expenses.manager.presentation.settings

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.expenses.manager.databinding.DialogUnableToFetchExchangeRatesBinding
import com.expenses.manager.extensions.hideSystemUI

class UnableToFetchExchangeRatesDialog(private val activity: Activity) : Dialog(activity) {

    private lateinit var binding: DialogUnableToFetchExchangeRatesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogUnableToFetchExchangeRatesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.buttonGotIt.setOnClickListener {
            dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        activity.hideSystemUI()
    }
}