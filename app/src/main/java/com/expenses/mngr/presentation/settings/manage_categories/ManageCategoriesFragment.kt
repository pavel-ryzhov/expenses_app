package com.expenses.mngr.presentation.settings.manage_categories

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
import com.expenses.mngr.R
import com.expenses.mngr.databinding.FragmentManageCategoriesBinding
import com.expenses.mngr.entities.category.Category
import com.expenses.mngr.extensions.navigateWithDefaultAnimation
import com.expenses.mngr.presentation.RecyclerViewItemDecoration
import com.expenses.mngr.presentation.dialogs.ConfirmActionDialog
import com.expenses.mngr.presentation.settings.manage_categories.add_category.AddCategoryFragment
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
                    it.second && it.third -> requireContext().getString(R.string.this_category_has_subcategories_and_expenses)
                    it.second -> requireContext().getString(R.string.this_category_has_subcategories)
                    it.third -> requireContext().getString(R.string.this_category_has_expenses)
                    else -> ""
                } + " " + requireContext().getString(R.string.are_you_sure_you_want_to_delete_category).format(it.first.name)
                ConfirmActionDialog(text, onConfirmed = {
                    manageCategoriesRecyclerAdapter.removeCategory(it.first)
                    viewModel.deleteCategory(it.first)
                }).show(requireActivity().supportFragmentManager, null)
            }
            categoryDeletedLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.category_deleted_successfully), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onManageCategoriesRecyclerViewItemClick(category: Category){
        findNavController().navigateWithDefaultAnimation(R.id.action_manageCategoriesFragment_to_addCategoryFragment, bundleOf(AddCategoryFragment.PARENT_CATEGORY_NAME_TAG to category.fullName))
    }
}