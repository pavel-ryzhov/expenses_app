package com.example.expenses.presentation.dialogs.amount_in_secondary_currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.databinding.AmountInSecondaryCurrenciesRecyclerViewItemBinding

class AmountInSecondaryCurrenciesRecyclerAdapter(
) : RecyclerView.Adapter<AmountInSecondaryCurrenciesRecyclerAdapter.Holder>() {

    private lateinit var data: Map<String, Double>
    private lateinit var iterator: Iterator<Map.Entry<String, Double>>

    fun setInitialData(map: Map<String, Double>){
        data = map
        iterator = data.iterator()
        notifyItemRangeInserted(0, data.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(AmountInSecondaryCurrenciesRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(iterator.next())
    }

    override fun getItemCount() = if (this::data.isInitialized) data.size else 0

    inner class Holder(val binding: AmountInSecondaryCurrenciesRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(entry: Map.Entry<String, Double>){
            binding.apply {
                textViewAmount.text = entry.value.toString()
                textViewCurrencyCode.text = entry.key
                constraintLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }
}