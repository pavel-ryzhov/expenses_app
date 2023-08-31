package com.example.expenses.presentation.settings.manage_categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.expenses.R
import com.example.expenses.databinding.ItemAddCategoryBinding
import com.example.expenses.databinding.ItemCategoryInManageCategoriesRecyclerViewBinding
import com.example.expenses.entities.category.Category

class ManageCategoriesRecyclerAdapter(
    private val provideCategories: (category: Category) -> Unit,
    private val onItemClick: (category: Category) -> Unit,
    private val onCrossClick: (category: Category) -> Unit,
) : RecyclerView.Adapter<ManageCategoriesRecyclerAdapter.Holder>() {

    companion object {
        private const val CATEGORY_ITEM = 0
        private const val ADD_CATEGORY_ITEM = 1
    }

    private lateinit var fadeIn: Animation
    private lateinit var fadeOut: Animation
    private lateinit var recyclerView: RecyclerView

    private val categories = mutableListOf<Category>()

    fun setCategories(categories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }

    fun setSubCategories(categories: List<Category>) {
        recyclerView.startAnimation(fadeOut)
        this.categories.clear()
        this.categories.addAll(categories)
    }

    fun removeCategory(category: Category){
        val index = categories.indexOf(category)
        categories.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemViewType(position: Int) =
        if (position == categories.size) ADD_CATEGORY_ITEM else CATEGORY_ITEM

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        fadeIn = AnimationUtils.loadAnimation(recyclerView.context, R.anim.fade_in)
            .apply { duration = 200 }
        fadeOut = AnimationUtils.loadAnimation(recyclerView.context, R.anim.fade_out)
            .apply { duration = 200 }
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                notifyDataSetChanged()
                recyclerView.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageCategoriesRecyclerAdapter.Holder {
        return Holder(
            if (viewType == CATEGORY_ITEM) ItemCategoryInManageCategoriesRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ) else ItemAddCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ManageCategoriesRecyclerAdapter.Holder, position: Int) {
        holder.setCategory(if (position < categories.size) categories[position] else null)
    }

    override fun getItemCount() = categories.size + 1

    inner class Holder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setCategory(category: Category? = null) {
            category?.let {
                if (binding is ItemCategoryInManageCategoriesRecyclerViewBinding) {
                    binding.textViewCategory.text = category.name
                    binding.crossView.setOnClickListener {
                        onCrossClick(category)
                    }
                    binding.textViewCategory.setOnClickListener {
                        onItemClick(category)
                    }
                    if (category.hasSubCategories()) {
                        binding.dropdownButtonView.visibility = View.VISIBLE
                        binding.dropdownButtonView.setOnClickListener {
                            provideCategories(category)
                        }
                    } else {
                        binding.dropdownButtonView.visibility = View.INVISIBLE
                    }
                }
            } ?: run {
                (binding as ItemAddCategoryBinding).textViewAddCategory.setOnClickListener {
                    onItemClick(if (categories.isNotEmpty()) categories.first().parent!! else Category.EMPTY_ROOT)
                }
            }
        }
    }
}