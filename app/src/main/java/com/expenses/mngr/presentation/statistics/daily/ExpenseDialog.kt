package com.expenses.mngr.presentation.statistics.daily

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
import com.expenses.mngr.data.preferences.AppPreferences
import com.expenses.mngr.databinding.DialogExpenseBinding
import com.expenses.mngr.entities.category.CategoryDBEntity
import com.expenses.mngr.entities.expense.Expense
import com.expenses.mngr.extensions.hideSystemUI
import com.expenses.mngr.utils.formatTime
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
            textViewAmount.setAmount(expense.amount)
            textViewDate.text = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).format(with(expense) {
                GregorianCalendar(
                    year,
                    month,
                    day,
                )
            }.time)
            textViewTime.text = formatTime(expense.hour, expense.minute)
            textViewCategory.text = CategoryDBEntity.getFriendlyName(expense.category)
            if (expense.description.isNotBlank())
                textViewDescription.text = expense.description
            else {
                textViewDescriptionLabel.height = 0
                textViewDescription.visibility = View.GONE
            }
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