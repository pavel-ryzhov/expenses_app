package com.example.expenses.presentation.statistics.total.all_expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.databinding.FragmentAllExpensesBinding
import com.example.expenses.entities.sorting.Sorting
import com.example.expenses.presentation.dialogs.DeleteExpenseDialog
import com.example.expenses.presentation.statistics.ExpensesRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllExpensesFragment : Fragment() {

    private lateinit var binding: FragmentAllExpensesBinding
    private val viewModel by viewModels<AllExpensesViewModel>()
    private var sorting: Sorting? = null
    private lateinit var expensesRecyclerAdapter: ExpensesRecyclerAdapter
    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllExpensesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expensesRecyclerAdapter = ExpensesRecyclerAdapter(true){
            DeleteExpenseDialog(it){
                expensesRecyclerAdapter.deleteExpense(it)
                fetchExpensesWithLoading()
            }.show(requireActivity().supportFragmentManager, null)
        }
        with(binding){
            filterView.setOnClickListener {
                SortingDialog({
                    sorting = it
                    fetchExpensesWithLoading(editTextDescription.text.toString())
                }, sorting ?: Sorting.DATE).show(requireActivity().supportFragmentManager, null)
            }
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            editTextDescription.doAfterTextChanged {
                fetchExpenses(it.toString())
            }
            recyclerViewExpenses.adapter = expensesRecyclerAdapter
            constraintLayoutLoading.setOnClickListener {}
        }

        subscribeOnLiveData()

        fetchExpensesWithLoading()
    }

    private fun fetchExpenses(description: String = ""){
        viewModel.fetchExpenses(description, sorting)
    }

    private fun fetchExpensesWithLoading(description: String = ""){
        binding.constraintLayoutLoading.visibility = View.VISIBLE
        fetchExpenses(description)
    }

    private fun subscribeOnLiveData(){
        with(viewModel) {
            expensesLiveData.observe(viewLifecycleOwner) {
                binding.constraintLayoutLoading.visibility = View.GONE
                expensesRecyclerAdapter.setExpenses(it)
            }
        }
    }
}