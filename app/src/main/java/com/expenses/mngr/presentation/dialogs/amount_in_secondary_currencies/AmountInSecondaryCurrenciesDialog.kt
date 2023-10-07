package com.expenses.mngr.presentation.dialogs.amount_in_secondary_currencies

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.expenses.mngr.data.preferences.AppPreferences
import com.expenses.mngr.databinding.DialogAmountInSecondaryCurrenciesBinding
import com.expenses.mngr.entities.expense.Amount
import com.expenses.mngr.extensions.hideSystemUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AmountInSecondaryCurrenciesDialog(
    private val amount: Amount
) : DialogFragment() {

    private lateinit var binding: DialogAmountInSecondaryCurrenciesBinding
    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).setView(onCreateView(layoutInflater, null, savedInstanceState)).create().apply {
            window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 50, 0, 50, 300))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAmountInSecondaryCurrenciesBinding.inflate(inflater)
        val adapter = AmountInSecondaryCurrenciesRecyclerAdapter(appPreferences.getDoubleRounding())
        binding.recyclerView.adapter = adapter
        adapter.setInitialData(amount)
        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        requireActivity().hideSystemUI()
    }
}