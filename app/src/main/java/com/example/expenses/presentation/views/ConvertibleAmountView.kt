package com.example.expenses.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.entities.expense.Amount
import com.example.expenses.extensions.roundAndFormat
import com.example.expenses.extensions.toActivity
import com.example.expenses.presentation.dialogs.amount_in_secondary_currencies.AmountInSecondaryCurrenciesDialog
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConvertibleAmountView(context: Context, attributeSet: AttributeSet) : MaterialTextView(context, attributeSet) {

    private lateinit var amount: Amount
    @Inject
    lateinit var appPreferences: AppPreferences

    init {
        setOnClickListener {
            if (::amount.isInitialized){
                AmountInSecondaryCurrenciesDialog(amount).show(context.toActivity().supportFragmentManager, null)
            }
        }
        text = "-.--"
    }
    @SuppressLint("SetTextI18n")
    fun setAmount(amount: Amount){
        this.amount = amount
        text = "${amount.get(appPreferences.getMainCurrency()).roundAndFormat(appPreferences.getDoubleRounding())} ${appPreferences.getMainCurrency()}"
    }
}