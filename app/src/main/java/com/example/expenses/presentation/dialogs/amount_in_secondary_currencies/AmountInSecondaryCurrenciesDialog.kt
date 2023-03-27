package com.example.expenses.presentation.dialogs.amount_in_secondary_currencies

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.expenses.R
import com.example.expenses.databinding.DialogAmountInSecondaryCurrenciesBinding
import com.example.expenses.extensions.hideSystemUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmountInSecondaryCurrenciesDialog(
    private val dataWrapper: DataWrapper,
    private val fragment: Fragment
) : DialogFragment() {
    fun show(amount: Double){
        subscribeOnLiveData()
        dataWrapper.provideValues(amount)
    }
    private fun subscribeOnLiveData(){
        fragment.apply {
            dataWrapper.valuesLiveData.observe(viewLifecycleOwner){
                val dialogLayout = layoutInflater.inflate(
                    R.layout.dialog_amount_in_secondary_currencies,
                    view!! as ViewGroup,
                    false
                )
                val dialogBinding = DialogAmountInSecondaryCurrenciesBinding.bind(dialogLayout)
                dialogBinding.recyclerView.adapter = AmountInSecondaryCurrenciesRecyclerAdapter(it)
                MaterialAlertDialogBuilder(requireContext()).setView(dialogLayout).setOnCancelListener { requireActivity().hideSystemUI() }.create().apply {
                    window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 50, 0, 50, 300))
                }.show()
            }
        }
    }
}