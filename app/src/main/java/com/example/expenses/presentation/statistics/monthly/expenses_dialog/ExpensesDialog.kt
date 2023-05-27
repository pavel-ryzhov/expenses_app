package com.example.expenses.presentation.statistics.monthly.expenses_dialog

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
import androidx.fragment.app.viewModels
import com.example.expenses.databinding.DialogExpensesBinding
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.extensions.hideSystemUI
import com.example.expenses.presentation.statistics.ExpensesRecyclerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ExpensesDialog(
    private val categoryName: String,
    private val calendar: Calendar
) : DialogFragment() {
    private lateinit var binding: DialogExpensesBinding
    private val viewModel by viewModels<ExpensesDialogViewModel>()
    private val adapter = ExpensesRecyclerAdapter(true)

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
        binding = DialogExpensesBinding.inflate(layoutInflater)
        binding.recyclerView.adapter = adapter
        binding.textViewCategory.text = CategoryDBEntity.getFriendlyName(categoryName)
        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        requireActivity().hideSystemUI()
    }

    override fun onResume() {
        subscribeOnLiveData()
        viewModel.fetchExpenses(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), categoryName)
        super.onResume()
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            expensesLiveData.observe(viewLifecycleOwner){
                adapter.setExpenses(it)
            }
        }
    }
}