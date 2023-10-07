package com.expenses.mngr.presentation.choose_secondary_currencies

import android.animation.LayoutTransition
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.expenses.mngr.R
import com.expenses.mngr.databinding.FragmentChooseSecondaryCurrenciesBinding
import com.expenses.mngr.entities.symbols.Symbol
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseSecondaryCurrenciesFragment : Fragment() {

    private lateinit var binding: FragmentChooseSecondaryCurrenciesBinding
    private val viewModel: ChooseSecondaryCurrenciesViewModel by viewModels()
    private val currenciesRecyclerAdapter =
        SecondaryCurrenciesRecyclerAdapter(this::onRecyclerViewCurrenciesItemClick)
    private val selectedCurrenciesRecyclerAdapter =
        SelectedSecondaryCurrenciesRecyclerAdapter(this::onRecyclerViewSelectedCurrenciesItemClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseSecondaryCurrenciesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val openedFromSettings = findNavController().previousBackStackEntry!!.destination.id == R.id.settingsFragment
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
            recyclerViewSelectedSymbols.apply {
                val linearLayoutManager = LinearLayoutManager(requireContext())
                layoutManager = linearLayoutManager
                adapter = selectedCurrenciesRecyclerAdapter
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
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }
            buttonDone.setOnClickListener {
                viewModel.saveSecondaryCurrencies(selectedCurrenciesRecyclerAdapter.getSelectedCurrencies(), openedFromSettings)
            }
            layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        }

        subscribeOnLiveData()

        if (openedFromSettings) viewModel.fetchSavedSecondaryCurrencies()
    }

    private fun onRecyclerViewCurrenciesItemClick(symbol: Symbol) {
        binding.recyclerViewSymbols.visibility = View.VISIBLE
        binding.recyclerViewSelectedSymbols.visibility = View.VISIBLE
        currenciesRecyclerAdapter.selectSymbol(symbol)
        selectedCurrenciesRecyclerAdapter.addSymbol(symbol)
        if (selectedCurrenciesRecyclerAdapter.itemCount == 1) {
            binding.recyclerViewSelectedSymbols.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_in
                )
            )
            binding.viewSeparator.apply {
                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
            }
        }
    }

    private fun onRecyclerViewSelectedCurrenciesItemClick(symbol: Symbol) {
        if (selectedCurrenciesRecyclerAdapter.itemCount == 1) {
            binding.viewSeparator.visibility = View.INVISIBLE
        }
        selectedCurrenciesRecyclerAdapter.removeSymbol(symbol)
        currenciesRecyclerAdapter.deselectSymbol(symbol)
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            symbolsLiveDataMediator.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    currenciesRecyclerAdapter.setSymbols(it)
                    binding.progressBarLoading.visibility = View.GONE
                }
            }
            savedSecondaryCurrenciesLiveData.observe(viewLifecycleOwner){
                it.forEach { symbol ->
                    onRecyclerViewCurrenciesItemClick(symbol)
                }
            }
            secondaryCurrenciesSavedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    if (it)
                        findNavController().navigateUp()
                    else
                        findNavController().navigate(R.id.action_chooseSecondaryCurrenciesFragment_to_generalFragment)
                }
            }
            noSelectedSecondaryCurrenciesLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.select_at_least_one_category), Toast.LENGTH_SHORT).show()
                }
            }
            nothingChangedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.nothing_changed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStop() {
        viewModel.notifyFragmentStopped()
        super.onStop()
    }
}