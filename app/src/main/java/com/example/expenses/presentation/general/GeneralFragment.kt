package com.example.expenses.presentation.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expenses.R
import com.example.expenses.databinding.FragmentGeneralBinding
import com.example.expenses.extensions.navigateWithDefaultAnimation
import com.example.expenses.presentation.NoNetworkHelper
import com.example.expenses.presentation.settings.ChangeMainCurrencyForegroundService
import com.example.expenses.presentation.settings.SettingsFragment
import com.example.expenses.utils.isServiceRunning
import com.github.mikephil.charting.components.Description
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GeneralFragment : Fragment() {
    private lateinit var binding: FragmentGeneralBinding
    private val viewModel: GeneralViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            buttonAddExpense.setOnClickListener {
                findNavController().navigateWithDefaultAnimation(R.id.action_generalFragment_to_addExpenseFragment)
            }
            buttonCurrencyCalculator.setOnClickListener {
                findNavController().navigateWithDefaultAnimation(R.id.action_generalFragment_to_currencyCalculatorFragment)
            }
            buttonStatistics.setOnClickListener {
                findNavController().navigateWithDefaultAnimation(R.id.action_generalFragment_to_monthlyStatisticsFragment)
            }
            buttonSettings.setOnClickListener {
                findNavController().navigateWithDefaultAnimation(R.id.action_generalFragment_to_settingsFragment)
            }
            expensesChartView.description = Description().apply {
                text = "This month statistics"
                textColor = ContextCompat.getColor(requireContext(), R.color.blue)
            }
            expensesChartView.animateXY(300, 500)
        }

        subscribeOnLiveData()

        if (isServiceRunning(requireContext(), ChangeMainCurrencyForegroundService::class.java))
            findNavController().navigateWithDefaultAnimation(R.id.action_generalFragment_to_settingsFragment, bundleOf(SettingsFragment.FOREGROUND_SERVICE_IS_RUNNING_TAG to true))
        else
            viewModel.fetchData()
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            totalTodayLiveData.observe(viewLifecycleOwner) {
                binding.textViewToday.text = it
            }
            totalThisMonthLiveData.observe(viewLifecycleOwner) {
                binding.textViewThisMonth.text = it
            }
            monthStatisticsLiveData.observe(viewLifecycleOwner) {
                binding.expensesChartView.setLineDataSet(it.apply {
                    color = ContextCompat.getColor(requireContext(), R.color.blue)
                    valueTextColor = ContextCompat.getColor(requireContext(), R.color.blue)
                    setCircleColor(ContextCompat.getColor(requireContext(), R.color.blue))
                    circleHoleColor = ContextCompat.getColor(requireContext(), R.color.milky_white)
                })
            }
            networkErrorLiveData.observe(viewLifecycleOwner) {
                if (it != null)
                    NoNetworkHelper.notifyNoNetwork(
                        requireActivity(),
                        binding.root,
                        viewModel.getAppPreferences()
                    )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.notifyFragmentStopped()
    }
}