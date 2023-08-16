package com.example.expenses.presentation.settings.choose_main_currency_dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenses.R
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.databinding.DialogChooseMainCurrencyBinding
import com.example.expenses.entities.symbols.Symbol
import com.example.expenses.extensions.hideSystemUI
import com.example.expenses.presentation.NoNetworkHelper
import com.example.expenses.presentation.choose_main_currency.CurrenciesRecyclerAdapter
import com.example.expenses.presentation.dialogs.ConfirmActionDialog
import com.example.expenses.presentation.settings.ChangeMainCurrencyForegroundService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseMainCurrencyDialog : DialogFragment() {

    @Inject
    lateinit var appPreferences: AppPreferences

    private val currenciesRecyclerAdapter = CurrenciesRecyclerAdapter(::onMainCurrencySelected)
    private val viewModel by viewModels<ChooseMainCurrencyViewModel>()
    private lateinit var binding: DialogChooseMainCurrencyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = DialogChooseMainCurrencyBinding.inflate(inflater).apply {
                recyclerViewSymbols.apply {
                    val linearLayoutManager = LinearLayoutManager(requireContext())
                    layoutManager = linearLayoutManager
                    adapter = currenciesRecyclerAdapter
                    addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            linearLayoutManager.orientation
                        ).apply {
                            setDrawable(
                                ColorDrawable(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.blue
                                    )
                                )
                            )
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
                buttonBack.setOnClickListener {
                    requireDialog().cancel()
                }
            }
        }
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
            window?.setBackgroundDrawable(
                InsetDrawable(
                    ColorDrawable(
                        Color.TRANSPARENT
                    ), 20, 50, 20, 130
                )
            )
        }
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            symbolsLiveData.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    it.remove(Symbol(appPreferences.getMainCurrency(), ""))
                    currenciesRecyclerAdapter.setSymbols(it)
                    binding.progressBarLoading.visibility = View.GONE
                    binding.buttonTryToReload.visibility = View.GONE
                }
            }
            fetchSymbolsErrorLiveData.observe(viewLifecycleOwner) {
                NoNetworkHelper.showSnackbarNoNetwork(binding.root)
                binding.progressBarLoading.visibility = View.GONE
                binding.buttonTryToReload.visibility = View.VISIBLE
            }
        }
    }

    private fun onMainCurrencySelected(symbol: Symbol) {
        ConfirmActionDialog(
            requireContext().getString(R.string.this_operation_may_take_some_time).format(symbol.code),
            onConfirmed = {
                val intent = Intent(requireContext(), ChangeMainCurrencyForegroundService::class.java).apply {
                    putExtra(ChangeMainCurrencyForegroundService.CURRENCY_TO_TAG, symbol.code)
                    action = ChangeMainCurrencyForegroundService.ACTION_CHANGE_MAIN_CURRENCY
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(intent)
                } else {
                    requireContext().startService(intent)
                }
                requireDialog().dismiss()
            },
            onCanceled = {
                requireDialog().dismiss()
            }).show(requireActivity().supportFragmentManager, null)
    }

    override fun onResume() {
        subscribeOnLiveData()
        viewModel.fetchSymbols()
        super.onResume()
    }

    override fun onDestroy() {
        requireActivity().hideSystemUI()
        super.onDestroy()
    }
}