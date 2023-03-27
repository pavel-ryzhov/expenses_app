package com.example.expenses.presentation.dialogs.amount_in_secondary_currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.databinding.AmountInSecondaryCurrenciesRecyclerViewItemBinding

class AmountInSecondaryCurrenciesRecyclerAdapter(
    private val data: Map<String, String>
) : RecyclerView.Adapter<AmountInSecondaryCurrenciesRecyclerAdapter.Holder>() {

    private val iterator = data.iterator()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(AmountInSecondaryCurrenciesRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(iterator.next())
    }

    override fun getItemCount() = data.size

    inner class Holder(val binding: AmountInSecondaryCurrenciesRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(entry: Map.Entry<String, String>){
            binding.apply {
                textViewAmount.text = entry.value
                textViewCurrencyCode.text = entry.key
                constraintLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }
}