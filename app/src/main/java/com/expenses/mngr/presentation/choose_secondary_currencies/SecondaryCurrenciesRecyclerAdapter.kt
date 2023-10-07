package com.expenses.mngr.presentation.choose_secondary_currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expenses.mngr.databinding.SecondaryCurrenciesRecyclerViewItemBinding
import com.expenses.mngr.entities.symbols.Symbol

class SecondaryCurrenciesRecyclerAdapter(private val onItemClick: (symbol: Symbol) -> Unit) :
    RecyclerView.Adapter<SecondaryCurrenciesRecyclerAdapter.Holder>() {

    private val symbols: MutableList<Symbol> = mutableListOf()
    private val sortedSymbols: MutableList<Symbol> = mutableListOf()
    private val selectedSymbols: MutableList<Symbol> = mutableListOf()
    private var sort = ""

    fun setSymbols(symbols: MutableList<Symbol>) {
        this.symbols.clear()
        this.symbols.addAll(symbols)
        sort()
    }

    fun selectSymbol(symbol: Symbol){
        selectedSymbols.add(symbol)
        sort()
    }

    fun deselectSymbol(symbol: Symbol){
        selectedSymbols.remove(symbol)
        sort()
    }

    fun setSort(string: String) {
        sort = string.lowercase()
        sort()
    }

    private fun sort() {
        sortedSymbols.clear()
        for (item in symbols) {
            if ((item.code.lowercase().contains(sort) || item.description.lowercase()
                    .contains(sort)) && !selectedSymbols.contains(item)
            ) {
                sortedSymbols.add(item)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            SecondaryCurrenciesRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setText(sortedSymbols[position])
    }

    override fun getItemCount() = sortedSymbols.size

    inner class Holder(private val binding: SecondaryCurrenciesRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setText(symbol: Symbol) {
            binding.checkboxSymbol.apply {
                text = "${symbol.code}: ${symbol.description}"
                isChecked = false
            }
            binding.frameLayout.setOnClickListener{
                onItemClick(symbol)
            }
        }
    }
}