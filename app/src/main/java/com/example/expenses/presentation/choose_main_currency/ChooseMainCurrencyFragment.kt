package com.example.expenses.presentation.choose_main_currency

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenses.R
import com.example.expenses.databinding.FragmentChooseMainCurrencyBinding
import com.example.expenses.entities.symbols.Symbol
import com.example.expenses.extensions.navigateWithDefaultAnimation
import com.example.expenses.presentation.NoNetworkHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseMainCurrencyFragment : Fragment() {

    private lateinit var binding: FragmentChooseMainCurrencyBinding
    private val viewModel: ChooseMainCurrencyViewModel by viewModels()
    private val currenciesRecyclerAdapter = CurrenciesRecyclerAdapter(this::onRecyclerViewItemClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseMainCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.saveDefaultCategories()
        binding.apply {
            recyclerViewSymbols.apply {
                val linearLayoutManager = LinearLayoutManager(requireContext())
                layoutManager = linearLayoutManager
                adapter = currenciesRecyclerAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        linearLayoutManager.orientation
                    ).apply {
                        setDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.blue)))
                    })
            }
            editTextSearch.addTextChangedListener(afterTextChanged = {
                currenciesRecyclerAdapter.setSort(it.toString())
            })
            buttonTryToReload.setOnClickListener {
                binding.buttonTryToReload.visibility = View.GONE
                binding.progressBarLoading.visibility = View.VISIBLE
                viewModel.fetchSymbols()
            }
        }
        subscribeOnLiveData()
        viewModel.fetchSymbols()
    }

    private fun onRecyclerViewItemClick(symbol: Symbol){
        viewModel.saveMainCurrency(symbol.code)
        findNavController().navigateWithDefaultAnimation(R.id.chooseSecondaryCurrenciesFragment)
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            symbolsLiveData.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    currenciesRecyclerAdapter.setSymbols(it)
                    binding.progressBarLoading.visibility = View.GONE
                    binding.buttonTryToReload.visibility = View.GONE
                }
            }
            fetchSymbolsErrorLiveData.observe(viewLifecycleOwner){
                NoNetworkHelper.notifyNoNetwork(requireActivity(), binding.root, viewModel.getAppPreferences())
                binding.progressBarLoading.visibility = View.GONE
                binding.buttonTryToReload.visibility = View.VISIBLE
            }
        }
    }
}