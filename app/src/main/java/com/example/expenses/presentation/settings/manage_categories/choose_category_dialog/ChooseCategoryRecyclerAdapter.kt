package com.example.expenses.presentation.settings.manage_categories.choose_category_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.expenses.R
import com.example.expenses.databinding.CategoryItemBinding
import com.example.expenses.databinding.ItemManageCategoriesBinding
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.google.android.material.button.MaterialButton

class ChooseCategoryRecyclerAdapter(
    private val buttonChooseThisCategory: MaterialButton,
    private val provideCategories: (category: Category) -> Unit,
    private val onItemClick: (category: Category) -> Unit = {},
    private val onManageCategoriesClick: () -> Unit = {},
    private val showManageCategoriesOption: Boolean = true,
    private val allowToChooseRoot: Boolean = false
) : RecyclerView.Adapter<ChooseCategoryRecyclerAdapter.Holder>() {

    companion object {
        private const val CATEGORY_ITEM = 0
        private const val MANAGE_CATEGORIES_ITEM = 1
    }

    private lateinit var fadeIn: Animation
    private lateinit var fadeOut: Animation
    private lateinit var recyclerView: RecyclerView

    private val categories = mutableListOf<Category>()

    fun setCategories(categories: List<Category>) {
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }

    fun setSubCategories(categories: List<Category>) {
        recyclerView.startAnimation(fadeOut)
        this.categories.clear()
        this.categories.addAll(categories)
        with(buttonChooseThisCategory) {
            if (allowToChooseRoot || !checkParentIsRoot(categories)) {
                if (visibility != View.VISIBLE) {
                    startAnimation(
                        AnimationUtils.loadAnimation(
                            recyclerView.context,
                            R.anim.fade_in
                        )
                    )
                    visibility = View.VISIBLE
                }
            } else {
                if (visibility != View.INVISIBLE) {
                    startAnimation(
                        AnimationUtils.loadAnimation(
                            recyclerView.context,
                            R.anim.fade_out
                        )
                    )
                    visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun getItemViewType(position: Int) =
        if (position == categories.size) MANAGE_CATEGORIES_ITEM else CATEGORY_ITEM

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
        buttonChooseThisCategory.setOnClickListener {
            if (allowToChooseRoot || !checkParentIsRoot(categories)) {
                onItemClick(categories.first().parent!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            if (viewType == CATEGORY_ITEM) CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ) else ItemManageCategoriesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setCategory(if (position < categories.size) categories[position] else null)
    }

    override fun getItemCount() = categories.size + if (showManageCategoriesOption) 1 else 0

    private fun checkParentIsRoot(categories: List<Category>) =
        categories.first().parent!!.toCategoryDBEntity() == CategoryDBEntity.ROOT

    inner class Holder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setCategory(category: Category? = null) {
            category?.let {
                if (binding is CategoryItemBinding) {
                    binding.textViewCategory.text = category.name
                    binding.textViewCategory.setOnClickListener {
                        onItemClick(category)
                    }
                    if (category.hasSubCategories()) {
                        binding.dropdownButtonView.visibility = View.VISIBLE
                        binding.view.visibility = View.INVISIBLE
                        binding.dropdownButtonView.setOnClickListener {
                            provideCategories(category)
                        }
                    } else {
                        binding.dropdownButtonView.visibility = View.GONE
                        binding.view.visibility = View.GONE
                    }
                }
            } ?: run {
                with((binding as ItemManageCategoriesBinding)) {
                    textViewManageCategories.setOnClickListener {
                        onManageCategoriesClick()
                    }
                }
            }
        }
    }
}