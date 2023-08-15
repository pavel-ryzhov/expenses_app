package com.example.expenses.presentation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.databinding.DialogDeleteExpenseBinding
import com.example.expenses.entities.expense.Expense
import com.example.expenses.extensions.hideSystemUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeleteExpenseDialog(private val expense: Expense, private val onDeleted: () -> Unit = {}) : DialogFragment() {

    private lateinit var binding: DialogDeleteExpenseBinding
    @Inject
    lateinit var expensesDao: ExpensesDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDeleteExpenseBinding.inflate(layoutInflater).apply {
            buttonDelete.setOnClickListener {
                CoroutineScope(Dispatchers.IO + Job()).launch{
                    expensesDao.deleteExpense(expense)
                    launch(Dispatchers.Main) {
                        onDeleted()
                    }
                }
                requireDialog().dismiss()
            }
            buttonCancel.setOnClickListener {
                requireDialog().cancel()
            }
        }
        return AlertDialog.Builder(requireContext()).setView(binding.root).create().apply {
            window?.setBackgroundDrawable(
                InsetDrawable(
                    ColorDrawable(
                        Color.TRANSPARENT
                    ), 20, 50, 20, 130
                )
            )
        }
    }

    override fun onStop() {
        requireActivity().hideSystemUI()
        super.onStop()
    }
}