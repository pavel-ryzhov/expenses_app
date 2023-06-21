package com.example.expenses.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expenses.R
import com.example.expenses.databinding.FragmentSettingsBinding
import com.example.expenses.extensions.navigateWithDefaultAnimation
import com.example.expenses.presentation.BackPressBlockable
import com.example.expenses.presentation.settings.choose_main_currency_dialog.ChooseMainCurrencyDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(), BackPressBlockable {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by viewModels<SettingsViewModel>()
    private var backPressAllowed = true

    companion object{
        const val FOREGROUND_SERVICE_IS_RUNNING_TAG = "FOREGROUND_SERVICE_IS_RUNNING_TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            editTextRounding.addTextChangedListener(afterTextChanged = {
                viewModel.saveRounding(it.toString())
            })
            textViewManageSecondaryCurrencies.setOnClickListener {
                findNavController().navigateWithDefaultAnimation(R.id.action_settingsFragment_to_chooseSecondaryCurrenciesFragment)
            }
            linearLayoutMainCurrency.setOnClickListener {
                ChooseMainCurrencyDialog().show(requireActivity().supportFragmentManager, null)
            }
            textViewManageCategories.setOnClickListener {
                findNavController().navigateWithDefaultAnimation(R.id.action_settingsFragment_to_manageCategoriesFragment)
            }
            constraintLayoutLoading.setOnClickListener {
            }
        }

        subscribeOnLiveData()

        arguments?.let {
            if (it.getBoolean(FOREGROUND_SERVICE_IS_RUNNING_TAG, false))
                onChangingMainCurrencyStarted()
        }

        viewModel.fetchData()
    }

    private fun onChangingMainCurrencyStarted(){
        binding.constraintLayoutLoading.visibility = View.VISIBLE
        backPressAllowed = false
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            mainCurrencyLiveData.observe(viewLifecycleOwner) {
                binding.textViewMainCurrency.text = it
            }
            roundingLiveData.observe(viewLifecycleOwner) {
                binding.editTextRounding.setText(it.toString())
            }
            fetchExchangeRatesErrorLiveData.observe(viewLifecycleOwner) {
                if (it != null)
                    UnableToFetchExchangeRatesDialog(requireActivity()).show()
            }
            mainCurrencySavedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    binding.constraintLayoutLoading.visibility = View.GONE
                    backPressAllowed = true
                    viewModel.fetchData()
                    Toast.makeText(
                        requireContext(),
                        "Main currency changed successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            changingMainCurrencyStartedLiveData.observe(viewLifecycleOwner) {
                if (it != null)
                    onChangingMainCurrencyStarted()
            }
            stateChangedLiveData.observe(viewLifecycleOwner) {
                binding.textViewLoading.text = it
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.notifyFragmentStopped()
    }

    override fun isBackPressAllowed(): Boolean {
        return backPressAllowed
    }
}