package com.example.expenses.presentation.choose_main_currency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.databinding.CurrenciesRecyclerViewItemBinding
import com.example.expenses.entities.symbols.Symbol

class CurrenciesRecyclerAdapter(private val onItemClick: (symbol: Symbol) -> Unit) : RecyclerView.Adapter<CurrenciesRecyclerAdapter.Holder>() {

    private val symbols: MutableList<Symbol> = mutableListOf()
    private val sortedSymbols: MutableList<Symbol> = mutableListOf()
    private var sort = ""

    fun setSymbols(symbols: MutableList<Symbol>) {
        this.symbols.clear()
        this.symbols.addAll(symbols)
        sort()
    }

    fun setSort(string: String) {
        sort = string.lowercase()
        sort()
    }

    private fun sort() {
        sortedSymbols.clear()
        for (item in symbols) {
            if (item.code.lowercase().contains(sort) || item.description.lowercase()
                    .contains(sort)
            ) {
                sortedSymbols.add(item)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            CurrenciesRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setSymbol(sortedSymbols[position])
    }

    override fun getItemCount() = sortedSymbols.size

    inner class Holder(private val binding: CurrenciesRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setSymbol(symbol: Symbol) {
            binding.textViewSymbol.apply {
                text = "${symbol.code}: ${symbol.description}"
                setOnClickListener{
                    onItemClick(symbol)
                }
            }
        }
    }
}