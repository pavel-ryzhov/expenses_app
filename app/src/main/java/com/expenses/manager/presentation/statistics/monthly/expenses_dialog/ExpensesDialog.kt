package com.expenses.manager.presentation.statistics.monthly.expenses_dialog

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
import com.expenses.manager.data.preferences.AppPreferences
import com.expenses.manager.databinding.DialogExpensesBinding
import com.expenses.manager.entities.category.CategoryDBEntity
import com.expenses.manager.entities.expense.Expense
import com.expenses.manager.extensions.hideSystemUI
import com.expenses.manager.presentation.dialogs.DeleteExpenseDialog
import com.expenses.manager.presentation.statistics.ExpensesRecyclerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpensesDialog(
    private val categoryName: String,
    private val calendar: Calendar,
    private val onExpenseDeleted: (expense: Expense) -> Unit
) : DialogFragment() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private lateinit var binding: DialogExpensesBinding
    private val viewModel by viewModels<ExpensesDialogViewModel>()
    private lateinit var adapter: ExpensesRecyclerAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        adapter = ExpensesRecyclerAdapter(true){
            DeleteExpenseDialog(it){
                adapter.deleteExpense(it)
                onExpenseDeleted(it)
            }.show(requireActivity().supportFragmentManager, null)
        }
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