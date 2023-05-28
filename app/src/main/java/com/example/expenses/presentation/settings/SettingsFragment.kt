package com.example.expenses.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expenses.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by viewModels<SettingsViewModel>()

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
        }

        subscribeOnLiveData()

        viewModel.fetchData()
    }

    private fun subscribeOnLiveData(){
        viewModel.apply {
            mainCurrencyLiveData.observe(viewLifecycleOwner){
                binding.textViewMainCurrency.text = it
            }
        }
    }
}