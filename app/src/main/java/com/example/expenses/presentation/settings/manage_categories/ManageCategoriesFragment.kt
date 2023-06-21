package com.example.expenses.presentation.settings.manage_categories

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expenses.R
import com.example.expenses.databinding.FragmentManageCategoriesBinding
import com.example.expenses.entities.category.Category
import com.example.expenses.extensions.navigateWithDefaultAnimation
import com.example.expenses.presentation.RecyclerViewItemDecoration
import com.example.expenses.presentation.dialogs.ConfirmActionDialog
import com.example.expenses.presentation.settings.manage_categories.add_category.AddCategoryFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageCategoriesFragment : Fragment() {

    private lateinit var binding: FragmentManageCategoriesBinding
    private val viewModel by viewModels<ManageCategoriesViewModel>()

    private lateinit var manageCategoriesRecyclerAdapter: ManageCategoriesRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageCategoriesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        manageCategoriesRecyclerAdapter = ManageCategoriesRecyclerAdapter(
            viewModel::provideCategories,
            this::onManageCategoriesRecyclerViewItemClick,
            viewModel::checkCategoryHasSubcategoriesAndExpenses
        )

        binding.apply {
            recyclerViewManageCategories.apply {
                adapter = manageCategoriesRecyclerAdapter
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
            }
            buttonBack.setOnClickListener {
                viewModel.provideParentCategories()
            }
            buttonClose.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        subscribeOnLiveData()

        viewModel.provideCategories()
    }

    private fun subscribeOnLiveData(){
        viewModel.apply {
            provideCategoriesLiveData.observe(viewLifecycleOwner) {
                if (it.isNotEmpty())
                    manageCategoriesRecyclerAdapter.setSubCategories(it)
                else
                    requireActivity().onBackPressed()
            }
            categoriesLiveData.observe(viewLifecycleOwner) {
                manageCategoriesRecyclerAdapter.setCategories(it)
            }
            checkCategoryHasSubCategoriesAndExpensesLiveData.observe(viewLifecycleOwner) {
                val text = when {
                    it.second && it.third -> "This category has subcategories and expenses."
                    it.second -> "This category has subcategories."
                    it.third -> "This category has expenses."
                    else -> ""
                } + " Are you sure you want to delete \"${it.first.name}\"?"
                ConfirmActionDialog(text, onConfirmed = {
                    manageCategoriesRecyclerAdapter.removeCategory(it.first)
                    viewModel.deleteCategory(it.first)
                }).show(requireActivity().supportFragmentManager, null)
            }
            categoryDeletedLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), "Category deleted successfully.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onManageCategoriesRecyclerViewItemClick(category: Category){
        findNavController().navigateWithDefaultAnimation(R.id.action_manageCategoriesFragment_to_addCategoryFragment, bundleOf(AddCategoryFragment.PARENT_CATEGORY_NAME_TAG to category.fullName))
    }
}