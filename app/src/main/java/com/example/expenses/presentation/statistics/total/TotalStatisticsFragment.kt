package com.example.expenses.presentation.statistics.total

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenses.R
import com.example.expenses.databinding.FragmentTotalStatisticsBinding
import com.example.expenses.extensions.navigateWithDefaultAnimation
import com.example.expenses.presentation.statistics.LegendRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TotalStatisticsFragment : Fragment() {

    private lateinit var binding: FragmentTotalStatisticsBinding
    private val viewModel by viewModels<TotalStatisticsViewModel>()
    private lateinit var legendRecyclerAdapter: LegendRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTotalStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        legendRecyclerAdapter = LegendRecyclerAdapter(
            viewModel::addCategoryFilter,
            viewModel::removeCategoryFilter
        )
        binding.apply {
            recyclerViewLegend.layoutManager = object : LinearLayoutManager(requireContext(), VERTICAL, false){
                override fun canScrollVertically() = false
            }
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonDaily.setOnClickListener {
                findNavController().navigate(R.id.action_totalStatisticsFragment_to_dailyStatisticsFragment)
            }
            buttonMonthly.setOnClickListener {
                findNavController().navigate(R.id.action_totalStatisticsFragment_to_monthlyStatisticsFragment)
            }
            textViewNumberOfExpensesLabel.setOnClickListener {
                openAllExpensesFragment()
            }
            textViewNumberOfExpenses.setOnClickListener {
                openAllExpensesFragment()
            }
            textViewNoExpenses.visibility = View.GONE
            constraintLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            recyclerViewLegend.adapter = legendRecyclerAdapter
        }
        subscribeOnLiveData()
        viewModel.fetchData()
    }
    private fun subscribeOnLiveData(){
        viewModel.apply {
            totalLiveData.observe(viewLifecycleOwner) {
                binding.textViewTotal.setAmount(it)
            }
            numberOfExpensesLiveData.observe(viewLifecycleOwner){
                binding.textViewNumberOfExpenses.text = it.toString()
            }
            pieChartStatisticsLiveData.observe(viewLifecycleOwner) {
                binding.pieChart.apply {
                    data = it
                    notifyDataSetChanged()
                    invalidate()
                    animateY(1000)
                }
            }
            legendLiveData.observe(viewLifecycleOwner){
                legendRecyclerAdapter.setRootCategory(it)
            }
            hasExpensesLiveData.observe(viewLifecycleOwner){
                binding.textViewNoExpenses.visibility = if (!it) View.VISIBLE else View.GONE
                binding.scrollView.visibility = if (it) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun openAllExpensesFragment(){
        findNavController().navigateWithDefaultAnimation(R.id.action_totalStatisticsFragment_to_allExpensesFragment)
    }
}