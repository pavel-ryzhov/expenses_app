package com.example.expenses.presentation.settings.manage_categories.choose_category_dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenses.R
import com.example.expenses.databinding.DialogChooseCategoryBinding
import com.example.expenses.entities.category.Category
import com.example.expenses.extensions.hideSystemUI
import com.example.expenses.presentation.RecyclerViewItemDecoration
import com.example.expenses.presentation.add_expense.AddExpenseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseCategoryDialog(
    private val onCategorySelected: (category: Category) -> Unit = {},
    private val onManageCategoriesClick: () -> Unit = {},
    private val showManageCategoriesOption: Boolean = true,
    private val allowToChooseRoot: Boolean = false
) : DialogFragment() {

    private lateinit var chooseCategoryRecyclerAdapter: ChooseCategoryRecyclerAdapter
    private var selectedCategory: Category? = null

    private lateinit var binding: DialogChooseCategoryBinding
    private val viewModel by viewModels<ChooseCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized)
            binding = DialogChooseCategoryBinding.inflate(inflater)
        if (!allowToChooseRoot) binding.buttonChooseThisCategory.visibility = View.INVISIBLE
        return binding.root.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).setView(
            onCreateView(
                layoutInflater,
                null,
                savedInstanceState
            )
        ).create().apply {
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window?.setBackgroundDrawable(InsetDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                ), 20, 50, 20, 130
            ))
        }
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            chooseCategoriesDialogLiveData.observe(viewLifecycleOwner) {
                binding.recyclerViewChooseCategory.apply {
                    chooseCategoryRecyclerAdapter = ChooseCategoryRecyclerAdapter(
                        binding.buttonChooseThisCategory,
                        viewModel::provideCategories,
                        this@ChooseCategoryDialog::onChooseCategoryRecyclerViewItemClick,
                        this@ChooseCategoryDialog::extendedOnManageCategoriesClick,
                        showManageCategoriesOption,
                        allowToChooseRoot
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
                binding.buttonBack.setOnClickListener {
                    provideParentCategories()
                }
                binding.buttonClose.setOnClickListener {
                    requireDialog().cancel()
                }
            }
            provideCategoriesLiveData.observe(viewLifecycleOwner) {
                if (it.isNotEmpty())
                    chooseCategoryRecyclerAdapter.setSubCategories(it)
                else
                    requireDialog().cancel()
            }
        }
    }

    private fun extendedOnManageCategoriesClick(){
        dismiss()
        onManageCategoriesClick()
    }

    private fun onChooseCategoryRecyclerViewItemClick(category: Category) {
        selectedCategory = category
        onCategorySelected(selectedCategory!!)
        requireDialog().cancel()
    }

    override fun onResume() {
        subscribeOnLiveData()
        viewModel.provideCategories()
        super.onResume()
    }

    override fun onDestroy() {
        requireActivity().hideSystemUI()
        super.onDestroy()
    }
}