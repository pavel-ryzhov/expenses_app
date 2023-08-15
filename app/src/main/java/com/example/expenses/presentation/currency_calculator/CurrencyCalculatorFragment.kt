package com.example.expenses.presentation.currency_calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expenses.R
import com.example.expenses.databinding.FragmentCurrencyCalculatorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyCalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCurrencyCalculatorBinding
    private val viewModel by viewModels<CurrencyCalculatorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrencyCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            autoCompleteTextViewCurrency1.setDropDownBackgroundResource(R.color.milky_white)
            autoCompleteTextViewCurrency2.setDropDownBackgroundResource(R.color.milky_white)
            autoCompleteTextViewCurrency3.setDropDownBackgroundResource(R.color.milky_white)
            autoCompleteTextViewCurrency4.setDropDownBackgroundResource(R.color.milky_white)
            editTextAmount1.addTextChangedListener {
                viewModel.notifyAmountChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_1)
            }
            editTextAmount2.addTextChangedListener {
                viewModel.notifyAmountChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_2)
            }
            editTextAmount3.addTextChangedListener {
                viewModel.notifyAmountChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_3)
            }
            editTextAmount4.addTextChangedListener {
                viewModel.notifyAmountChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_4)
            }
            autoCompleteTextViewCurrency1.addTextChangedListener {
                viewModel.notifyCurrencyChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_1, editTextAmount1.text.toString())
            }
            autoCompleteTextViewCurrency2.addTextChangedListener {
                viewModel.notifyCurrencyChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_2, editTextAmount2.text.toString())
            }
            autoCompleteTextViewCurrency3.addTextChangedListener {
                viewModel.notifyCurrencyChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_3, editTextAmount3.text.toString())
            }
            autoCompleteTextViewCurrency4.addTextChangedListener {
                viewModel.notifyCurrencyChanged(it.toString(), CurrencyCalculatorViewModel.EDITTEXT_4, editTextAmount4.text.toString())
            }
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        subscribeOnLiveData()
        viewModel.fetchCurrencies()
    }

    private fun subscribeOnLiveData(){
        viewModel.apply {
            currenciesLiveData.observe(viewLifecycleOwner){ list ->
                list[0]?.let {
                    binding.autoCompleteTextViewCurrency1.setText(it)
                    binding.autoCompleteTextViewCurrency1.setAdapter(getAutoCompleteTextViewCurrencyAdapter(list))
                }
                list[1]?.let {
                    binding.apply {
                        autoCompleteTextViewCurrency2.setText(it)
                        autoCompleteTextViewCurrency2.setAdapter(getAutoCompleteTextViewCurrencyAdapter(list))
                        textInputLayoutAmount2.visibility = View.VISIBLE
                        textInputLayoutCurrency2.visibility = View.VISIBLE
                    }
                }
                list[2]?.let {
                    binding.apply {
                        autoCompleteTextViewCurrency3.setText(it)
                        autoCompleteTextViewCurrency3.setAdapter(getAutoCompleteTextViewCurrencyAdapter(list))
                        textInputLayoutAmount3.visibility = View.VISIBLE
                        textInputLayoutCurrency3.visibility = View.VISIBLE
                    }
                }
                list[3]?.let {
                    binding.apply {
                        autoCompleteTextViewCurrency4.setText(it)
                        autoCompleteTextViewCurrency4.setAdapter(getAutoCompleteTextViewCurrencyAdapter(list))
                        textInputLayoutAmount4.visibility = View.VISIBLE
                        textInputLayoutCurrency4.visibility = View.VISIBLE
                    }
                }
            }
            amountLiveData.observe(viewLifecycleOwner){
                binding.apply {
                    if (it[0] != null) editTextAmount1.setText(it[0])
                    if (it[1] != null) editTextAmount2.setText(it[1])
                    if (it[2] != null) editTextAmount3.setText(it[2])
                    if (it[3] != null) editTextAmount4.setText(it[3])
                }
                notifyDataSet()
            }
        }
    }
    private fun getAutoCompleteTextViewCurrencyAdapter(list: List<String?>) = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return super.getView(position, convertView, parent).apply { textAlignment = View.TEXT_ALIGNMENT_CENTER }
        }
    }.apply { addAll(list.filterNotNull()) }
}