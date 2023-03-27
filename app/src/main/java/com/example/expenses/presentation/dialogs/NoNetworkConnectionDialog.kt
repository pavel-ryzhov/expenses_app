package com.example.expenses.presentation.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.databinding.DialogNoNetworkConnectionBinding
import com.example.expenses.extensions.hideSystemUI

class NoNetworkConnectionDialog(
    private val activity: Activity,
    private val appPreferences: AppPreferences
) : Dialog(activity) {

    private lateinit var binding: DialogNoNetworkConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoNetworkConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.buttonGotIt.setOnClickListener {
            dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        appPreferences.saveShowDialogOnNetworkError(!binding.checkboxDoNotShowThisDialogAgain.isChecked)
        activity.hideSystemUI()
    }
}