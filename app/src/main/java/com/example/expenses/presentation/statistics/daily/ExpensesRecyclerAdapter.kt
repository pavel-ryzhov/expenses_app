package com.example.expenses.presentation.statistics.daily

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.data.preferences.AppPreferencesImpl
import com.example.expenses.databinding.ItemExpenseBinding
import com.example.expenses.entities.expense.Expense
import com.example.expenses.extensions.roundAndFormat
import com.example.expenses.extensions.toActivity
import com.example.expenses.utils.formatTime

class ExpensesRecyclerAdapter : RecyclerView.Adapter<ExpensesRecyclerAdapter.Holder>() {

    private val expenses = mutableListOf<Expense>()

    fun setExpenses(expenses: List<Expense>){
        this.expenses.addAll(expenses)
        notifyItemRangeInserted(0, expenses.size)
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
                textViewAmount.text = "${expense.amount.roundAndFormat()} ${AppPreferencesImpl(binding.root.context).getMainCurrency().code}"
                textViewTime.text = formatTime(expense.hour, expense.minute)
                root.setOnClickListener {
                    ExpenseDialog(expense).show(root.context.toActivity().supportFragmentManager, null)
                }
            }
        }
    }
}