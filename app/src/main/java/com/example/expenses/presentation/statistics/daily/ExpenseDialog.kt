package com.example.expenses.presentation.statistics.daily

import android.annotation.SuppressLint
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
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.databinding.DialogExpenseBinding
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.entities.expense.Expense
import com.example.expenses.extensions.hideSystemUI
import com.example.expenses.extensions.roundAndFormat
import com.example.expenses.utils.formatTime
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseDialog(
    private val expense: Expense
) : DialogFragment() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private lateinit var binding: DialogExpenseBinding

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogExpenseBinding.inflate(inflater)
        return binding.apply {
            textViewAmount.text =
                "${expense.amount.roundAndFormat()} ${appPreferences.getMainCurrency().code}"
            textViewDate.text = SimpleDateFormat("MMMM d, yyyy").format(with(expense) {
                GregorianCalendar(
                    year,
                    month,
                    day,
                )
            }.time)
            textViewTime.text = formatTime(expense.hour, expense.minute)
            textViewCategory.text = CategoryDBEntity.getFriendlyName(expense.category)
            textViewDescription.text = expense.description
        }.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).setView(
            onCreateView(
                layoutInflater,
                null,
                savedInstanceState
            )
        ).create().apply {
            window?.setBackgroundDrawable(
                InsetDrawable(
                    ColorDrawable(Color.TRANSPARENT),
                    50,
                    0,
                    50,
                    300
                )
            )
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        requireActivity().hideSystemUI()
    }
}