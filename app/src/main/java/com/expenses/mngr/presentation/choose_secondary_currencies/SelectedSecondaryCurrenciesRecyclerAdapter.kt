package com.expenses.mngr.presentation.choose_secondary_currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.expenses.mngr.R
import com.expenses.mngr.databinding.SecondaryCurrenciesRecyclerViewItemBinding
import com.expenses.mngr.entities.symbols.Symbol

class SelectedSecondaryCurrenciesRecyclerAdapter(private val onItemClick: (symbol: Symbol) -> Unit) :
    RecyclerView.Adapter<SelectedSecondaryCurrenciesRecyclerAdapter.Holder>() {

    private val selectedSymbols: MutableList<Symbol> = mutableListOf()

    fun getSelectedCurrencies() = selectedSymbols

    fun addSymbol(symbol: Symbol) {
        selectedSymbols.add(symbol)
        notifyItemInserted(selectedSymbols.size - 1)
    }

    fun removeSymbol(symbol: Symbol) {
        selectedSymbols.remove(symbol)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        SecondaryCurrenciesRecyclerViewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setSymbol(selectedSymbols[position])
    }

    override fun getItemCount() = selectedSymbols.size

    inner class Holder(private val binding: SecondaryCurrenciesRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setSymbol(symbol: Symbol) {
            binding.checkboxSymbol.apply {
                text = "${symbol.code}: ${symbol.description}"
                isChecked = true
            }
            binding.frameLayout.setOnClickListener {
                val animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.fade_out)
                binding.checkboxSymbol.startAnimation(animation)
                onItemClick(symbol)
                binding.checkboxSymbol.postDelayed({
                    notifyDataSetChanged()
                }, animation.duration)
            }
        }
    }
}