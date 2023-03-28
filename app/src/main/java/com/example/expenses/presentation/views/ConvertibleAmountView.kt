package com.example.expenses.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import com.example.expenses.extensions.toActivity
import com.example.expenses.presentation.dialogs.amount_in_secondary_currencies.AmountInSecondaryCurrenciesDialog
import com.google.android.material.textview.MaterialTextView

class ConvertibleAmountView(context: Context, attributeSet: AttributeSet) : MaterialTextView(context, attributeSet) {
    init {
        setOnClickListener {
            if (text.isNotBlank()){
                AmountInSecondaryCurrenciesDialog(text.split(" ")[0].toDouble()).show(context.toActivity().supportFragmentManager, null)
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun setAmount(amount: Double, currencyCode: String){
        text = "$amount $currencyCode"
    }
}