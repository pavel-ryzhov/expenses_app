package com.expenses.mngr.presentation.statistics

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expenses.mngr.databinding.ItemExpenseBinding
import com.expenses.mngr.entities.expense.Expense
import com.expenses.mngr.extensions.toActivity
import com.expenses.mngr.presentation.statistics.daily.ExpenseDialog
import com.expenses.mngr.utils.formatDate
import com.expenses.mngr.utils.formatTime

class ExpensesRecyclerAdapter(private val typeDate: Boolean = false, private val onExpenseDeleted: (expense: Expense) -> Unit) : RecyclerView.Adapter<ExpensesRecyclerAdapter.Holder>() {

    private val expenses = mutableListOf<Expense>()

    @SuppressLint("NotifyDataSetChanged")
    fun setExpenses(expenses: List<Expense>){
        this.expenses.clear()
        this.expenses.addAll(expenses)
        notifyDataSetChanged()
    }

    fun deleteExpense(expense: Expense){
        val pos = expenses.indexOf(expense)
        expenses.removeAt(pos)
        notifyItemRemoved(pos)
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
                textViewAmount.setAmount(expense.amount)
                textViewTimeOrDate.text = if (typeDate) formatDate(expense.month, expense.day) else formatTime(expense.hour, expense.minute)
                root.setOnClickListener {
                    ExpenseDialog(expense).show(root.context.toActivity().supportFragmentManager, null)
                }
                root.setOnLongClickListener {
                    onExpenseDeleted(expense)
                    true
                }
            }
        }
    }
}