package com.example.expenses.presentation.statistics

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.data.preferences.AppPreferencesImpl
import com.example.expenses.databinding.ItemExpenseBinding
import com.example.expenses.entities.expense.Expense
import com.example.expenses.extensions.roundAndFormat
import com.example.expenses.extensions.toActivity
import com.example.expenses.presentation.statistics.daily.ExpenseDialog
import com.example.expenses.utils.formatDate
import com.example.expenses.utils.formatTime

class ExpensesRecyclerAdapter(private val typeDate: Boolean = false, private val rounding: Int = 2) : RecyclerView.Adapter<ExpensesRecyclerAdapter.Holder>() {

    private val expenses = mutableListOf<Expense>()

    @SuppressLint("NotifyDataSetChanged")
    fun setExpenses(expenses: List<Expense>){
        this.expenses.clear()
        this.expenses.addAll(expenses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setExpense(expenses[position])
    }

    override fun getItemCount() = expenses.size

    inner class Holder(private val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun setExpense(expense: Expense){
            binding.apply {
                textViewAmount.text = "${expense.amount.roundAndFormat(rounding)} ${AppPreferencesImpl(binding.root.context).getMainCurrency()}"
                textViewTimeOrDate.text = if (typeDate) formatDate(expense.month, expense.day) else formatTime(expense.hour, expense.minute)
                root.setOnClickListener {
                    ExpenseDialog(expense).show(root.context.toActivity().supportFragmentManager, null)
                }
            }
        }
    }
}