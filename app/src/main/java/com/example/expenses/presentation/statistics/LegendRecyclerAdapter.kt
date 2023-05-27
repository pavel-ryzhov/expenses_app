package com.example.expenses.presentation.statistics

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.databinding.ItemLegendBinding
import com.example.expenses.entities.category.Category

class LegendRecyclerAdapter(
    private val onFilterAdded: (category: Category) -> Unit,
    private val onFilterRemoved: (category: Category) -> Unit
) : RecyclerView.Adapter<LegendRecyclerAdapter.Holder>() {

    private val displayedCategories = mutableListOf<Category>()
    private lateinit var recyclerView: RecyclerView

    fun setRootCategory(rootCategory: Category) {
        displayedCategories.clear()
        displayedCategories.addAll(rootCategory.subCategories)
        notifyDataSetChanged()
    }

    private fun openCategory(category: Category) {
        val index = displayedCategories.indexOf(category) + 1
        displayedCategories.addAll(index, category.subCategories)
        notifyItemRangeInserted(index, category.subCategories.size)
    }

    private fun closeCategory(category: Category) {
        val index = displayedCategories.indexOf(category) + 1
        var removedItems = 0
        while (displayedCategories[index].fullName.startsWith(category.fullName)) {
            displayedCategories.removeAt(index)
            removedItems++
        }
        notifyItemRangeRemoved(index, removedItems)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemLegendBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setCategory(displayedCategories[position])
    }

    override fun getItemCount() = displayedCategories.size

    private fun findHolderByIndex(index: Int): Holder =
        recyclerView.findViewHolderForAdapterPosition(index) as Holder

    inner class Holder(private val binding: ItemLegendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setCategory(category: Category) {
            binding.apply {
                imageViewLabelColor.background = ColorDrawable(category.color)
                textViewLabel.text = category.name
                dropdownButtonView.visibility =
                    if (category.hasSubCategories()) VISIBLE else INVISIBLE
                constraintLayout.setOnClickListener {
                    val visibility = if (view.visibility == GONE) {
                        onFilterAdded(category)
                        VISIBLE
                    } else {
                        onFilterRemoved(category)
                        GONE
                    }
                    setViewVisibility(visibility)
                    setViewVisibilityByCategory(category, visibility)
                }
                if (category.hasSubCategories()) {
                    dropdownButtonView.setOnRolledDown {
                        setMarginTop(24)
                        val index = displayedCategories.indexOf(category)
                        if (index < displayedCategories.size - 1) findHolderByIndex(index + 1).setMarginTop(24)
                        openCategory(category)
                    }
                    dropdownButtonView.setOnRolledUp {
                        closeCategory(category)
                        setMarginTop(0)
                        if (category.parent!!.parent == null) {
                            val index = displayedCategories.indexOf(category)
                            if (index < displayedCategories.size - 1) findHolderByIndex(index + 1).setMarginTop(0)
                        }
                    }
                }
            }
        }

        private fun setMarginTop(margin: Int) {
            (binding.constraintLayout.layoutParams as RecyclerView.LayoutParams).topMargin = margin
        }

        private fun setViewVisibility(visibility: Int) {
            binding.view.visibility = visibility
        }

        private fun setViewVisibilityByCategory(category: Category, visibility: Int) {
            if (category.hasSubCategories()) {
                for (c in category.subCategories) {
                    val index = displayedCategories.indexOf(c)
                    if (index != -1) {
                        findHolderByIndex(index).setViewVisibility(visibility)
                        setViewVisibilityByCategory(c, visibility)
                    } else break
                }
            }
        }
    }
}