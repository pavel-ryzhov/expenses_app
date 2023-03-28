package com.example.expenses.presentation.add_expense

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expenses.R
import com.example.expenses.databinding.FragmentAddExpenseBinding
import com.example.expenses.entities.category.Category
import com.example.expenses.extensions.getCenterXChildPosition
import com.example.expenses.presentation.DateRecyclerAdapter
import com.example.expenses.presentation.dialogs.choose_category_dialog.ChooseCategoryDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddExpenseFragment : Fragment() {
    private lateinit var binding: FragmentAddExpenseBinding
    private val viewModel: AddExpenseViewModel by viewModels()
    private lateinit var dateRecyclerAdapter: DateRecyclerAdapter
    private lateinit var autoCompleteTextViewCurrenciesAdapter: ArrayAdapter<String>
    private var selectedCategory: Category? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dateRecyclerAdapter = DateRecyclerAdapter(
            SimpleDateFormat("MMMM d"),
            Calendar.DAY_OF_MONTH,
            fragmentManager = requireActivity().supportFragmentManager
        )
        autoCompleteTextViewCurrenciesAdapter = object :
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent)
                    .apply { textAlignment = View.TEXT_ALIGNMENT_CENTER }
            }
        }
        binding.apply {
            recyclerViewDate.adapter = dateRecyclerAdapter
            arrowLeft.setOnClickListener {
                recyclerViewDate.smoothScrollToPosition(recyclerViewDate.getCenterXChildPosition() - 1)
            }
            arrowRight.setRight()
            arrowRight.setOnClickListener {
                recyclerViewDate.smoothScrollToPosition(recyclerViewDate.getCenterXChildPosition() + 1)
            }
            autoCompleteTextViewCurrency.setAdapter(autoCompleteTextViewCurrenciesAdapter)
            autoCompleteTextViewCurrency.setDropDownBackgroundResource(R.color.milky_white)
            textInputLayoutCategory.setEndIconOnClickListener {
                showChooseCategoryDialog()
            }
            autoCompleteTextViewCategory.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    showChooseCategoryDialog()
                }
                true
            }
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonDone.setOnClickListener {
                viewModel.addExpense(
                    dateRecyclerAdapter.getCurrentDate(),
                    editTextAmount.text.toString(),
                    autoCompleteTextViewCurrency.text.toString(),
                    selectedCategory,
                    editTextDescription.text.toString()
                )
            }
        }
        subscribeOnLiveData()

        viewModel.fetchCurrencies()
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            currenciesLiveData.observe(viewLifecycleOwner) { symbols ->
                autoCompleteTextViewCurrenciesAdapter.addAll(symbols.map { it.code })
                binding.autoCompleteTextViewCurrency.setText(symbols[symbols.size - 1].code, false)
                val layoutParams = binding.autoCompleteTextViewCurrency.layoutParams
                layoutParams.height = binding.editTextAmount.height
                binding.autoCompleteTextViewCurrency.layoutParams = layoutParams
            }
            expenseAddedSuccessfullyLiveData.observe(viewLifecycleOwner) {
                requireActivity().onBackPressed()
                Toast.makeText(requireContext(), "Expense added successfully.", Toast.LENGTH_SHORT)
                    .show()
            }
            amountFieldIsEmptyLiveData.observe(viewLifecycleOwner) {
                binding.textInputLayoutAmount.apply {
                    error = "Enter amount!"
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
            categoryFieldIsEmptyLiveData.observe(viewLifecycleOwner) {
                binding.textInputLayoutCategory.apply {
                    error = "Choose category!"
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
        }
    }

    private fun showChooseCategoryDialog() {
        ChooseCategoryDialog {
            selectedCategory = it
            binding.autoCompleteTextViewCategory.setText(it.name)
        }.show(requireActivity().supportFragmentManager, null)
    }
}