package com.expenses.manager.presentation.add_expense

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
import androidx.navigation.fragment.findNavController
import com.expenses.manager.R
import com.expenses.manager.data.preferences.AppPreferences
import com.expenses.manager.databinding.FragmentAddExpenseBinding
import com.expenses.manager.entities.category.Category
import com.expenses.manager.extensions.getCenterXChildPosition
import com.expenses.manager.extensions.navigateWithDefaultAnimation
import com.expenses.manager.presentation.BackPressBlockable
import com.expenses.manager.presentation.DateRecyclerAdapter
import com.expenses.manager.presentation.NoNetworkHelper
import com.expenses.manager.presentation.settings.manage_categories.choose_category_dialog.ChooseCategoryDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AddExpenseFragment : Fragment(), BackPressBlockable {
    private lateinit var binding: FragmentAddExpenseBinding
    private val viewModel: AddExpenseViewModel by viewModels()
    private lateinit var dateRecyclerAdapter: DateRecyclerAdapter
    private lateinit var autoCompleteTextViewCurrenciesAdapter: ArrayAdapter<String>
    private var selectedCategory: Category? = null
    private var backPressAllowed = true

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dateRecyclerAdapter = DateRecyclerAdapter(
            SimpleDateFormat("MMMM d", Locale.ENGLISH),
            Calendar.DAY_OF_MONTH
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
            constraintLayoutLoading.setOnClickListener {
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
                autoCompleteTextViewCurrenciesAdapter.addAll(symbols)
                binding.autoCompleteTextViewCurrency.setText(symbols[symbols.size - 1], false)
                val layoutParams = binding.autoCompleteTextViewCurrency.layoutParams
                layoutParams.height = binding.editTextAmount.height
                binding.autoCompleteTextViewCurrency.layoutParams = layoutParams
            }
            expenseAddedSuccessfullyLiveData.observe(viewLifecycleOwner) {
                hideLoadingLayout()
                requireActivity().onBackPressed()
                Toast.makeText(requireContext(), requireContext().getString(R.string.expense_added_successfully), Toast.LENGTH_SHORT)
                    .show()
            }
            amountFieldIsEmptyLiveData.observe(viewLifecycleOwner) {
                binding.textInputLayoutAmount.apply {
                    error = requireContext().getString(R.string.enter_amount)
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
            categoryFieldIsEmptyLiveData.observe(viewLifecycleOwner) {
                binding.textInputLayoutCategory.apply {
                    error = requireContext().getString(R.string.choose_category)
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
            amountIsZeroLiveData.observe(viewLifecycleOwner) {
                binding.textInputLayoutAmount.apply {
                    error = requireContext().getString(R.string.amount_cannot_be_zero)
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
            addingExpenseStartedLiveData.observe(viewLifecycleOwner){
                it?.let {
                    binding.constraintLayoutLoading.visibility = View.VISIBLE
                    backPressAllowed = false
                }
            }
            networkErrorLiveData.observe(viewLifecycleOwner){
                if (it != null) {
                    hideLoadingLayout()
                    NoNetworkHelper.notifyNoNetwork(
                        requireActivity(),
                        binding.root,
                        appPreferences
                    )
                }
            }
        }
    }

    private fun hideLoadingLayout() {
        binding.constraintLayoutLoading.visibility = View.GONE
        backPressAllowed = true
    }

    private fun showChooseCategoryDialog() {
        ChooseCategoryDialog ({
            selectedCategory = it
            binding.autoCompleteTextViewCategory.setText(it.name)
        }, {
            findNavController().navigateWithDefaultAnimation(R.id.action_addExpenseFragment_to_manageCategoriesFragment)
        }).show(requireActivity().supportFragmentManager, null)
    }

    override fun onStop() {
        super.onStop()
        viewModel.notifyFragmentStopped()
    }

    override fun isBackPressAllowed(): Boolean {
        return backPressAllowed
    }
}