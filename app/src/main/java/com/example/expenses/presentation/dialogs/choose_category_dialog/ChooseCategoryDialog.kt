package com.example.expenses.presentation.dialogs.choose_category_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenses.R
import com.example.expenses.databinding.DialogChooseCategoryBinding
import com.example.expenses.entities.category.Category
import com.example.expenses.extensions.hideSystemUI
import com.example.expenses.presentation.views.RecyclerViewItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChooseCategoryDialog(
    private val categoriesDaoWrapper: CategoriesDaoWrapper,
    private val fragment: Fragment,
) {

    val categorySelectedLiveData = MutableLiveData<Category>()

    private lateinit var chooseCategoryRecyclerAdapter: ChooseCategoryRecyclerAdapter
    private lateinit var chooseCategoryDialog: AlertDialog
    private var selectedCategory: Category? = null

    fun show() {
        subscribeOnLiveData()
        categoriesDaoWrapper.provideCategories()
    }

    private fun subscribeOnLiveData() {
        categoriesDaoWrapper.apply {
            fragment.apply {
                chooseCategoriesDialogLiveData.observe(viewLifecycleOwner) {
                    val dialogLayout = layoutInflater.inflate(
                        R.layout.dialog_choose_category,
                        view!! as ViewGroup,
                        false
                    )
                    dialogLayout.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    val dialogBinding = DialogChooseCategoryBinding.bind(dialogLayout)
                    chooseCategoryDialog =
                        MaterialAlertDialogBuilder(requireContext()).setView(dialogLayout)
                            .setOnCancelListener {
                                requireActivity().hideSystemUI()
                            }.create()
                    dialogBinding.recyclerViewChooseCategory.apply {
                        chooseCategoryRecyclerAdapter = ChooseCategoryRecyclerAdapter(
                            categoriesDaoWrapper::provideCategories,
                            this@ChooseCategoryDialog::onChooseCategoryRecyclerViewItemClick
                        )
                        this.adapter = chooseCategoryRecyclerAdapter
                        val linearLayoutManager = LinearLayoutManager(requireContext())
                        layoutManager = linearLayoutManager
                        addItemDecoration(
                            RecyclerViewItemDecoration(
                                ColorDrawable(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.blue
                                    )
                                )
                            )
                        )
                        chooseCategoryRecyclerAdapter.setCategories(it)
                    }
                    dialogBinding.buttonBack.setOnClickListener {
                        categoriesDaoWrapper.provideParentCategories()
                    }
                    dialogBinding.buttonClose.setOnClickListener {
                        chooseCategoryDialog.cancel()
                    }
                    chooseCategoryDialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    chooseCategoryDialog.window?.setBackgroundDrawable(
                        InsetDrawable(
                            ColorDrawable(
                                Color.TRANSPARENT
                            ), 20, 50, 20, 130
                        )
                    )
                    chooseCategoryDialog.show()
                }
                provideCategoriesLiveData.observe(viewLifecycleOwner){
                    if (it.isNotEmpty())
                        chooseCategoryRecyclerAdapter.setSubCategories(it)
                    else
                        chooseCategoryDialog.cancel()
                }
            }
        }
    }


    private fun onChooseCategoryRecyclerViewItemClick(category: Category) {
        selectedCategory = category
        categorySelectedLiveData.postValue(selectedCategory)
        chooseCategoryDialog.cancel()
    }
}