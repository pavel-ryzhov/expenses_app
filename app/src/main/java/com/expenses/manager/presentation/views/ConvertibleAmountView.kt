package com.expenses.manager.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.expenses.manager.data.preferences.AppPreferences
import com.expenses.manager.entities.expense.Amount
import com.expenses.manager.extensions.roundAndFormat
import com.expenses.manager.extensions.toActivity
import com.expenses.manager.presentation.dialogs.amount_in_secondary_currencies.AmountInSecondaryCurrenciesDialog
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