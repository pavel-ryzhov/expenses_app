package com.example.expenses.presentation.dialogs.amount_in_secondary_currencies

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
import com.example.expenses.databinding.DialogAmountInSecondaryCurrenciesBinding
import com.example.expenses.extensions.hideSystemUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmountInSecondaryCurrenciesDialog(
    private val amount: MutableMap<String, Double>
) : DialogFragment() {

    private lateinit var binding: DialogAmountInSecondaryCurrenciesBinding
    private val adapter = AmountInSecondaryCurrenciesRecyclerAdapter()

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
        binding.recyclerView.adapter = adapter
        adapter.setInitialData(amount)
        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        requireActivity().hideSystemUI()
    }
}