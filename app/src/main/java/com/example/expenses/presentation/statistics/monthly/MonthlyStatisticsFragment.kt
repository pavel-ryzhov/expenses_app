package com.example.expenses.presentation.statistics.monthly

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.R
import com.example.expenses.databinding.FragmentMonthlyStatisticsBinding
import com.example.expenses.extensions.getCenterXChildPosition
import com.example.expenses.presentation.DateRecyclerAdapter
import com.example.expenses.presentation.dialogs.amount_in_secondary_currencies.AmountInSecondaryCurrenciesDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MonthlyStatisticsFragment : Fragment() {

    private lateinit var binding: FragmentMonthlyStatisticsBinding
    private val viewModel by viewModels<MonthlyStatisticsViewModel>()
    private lateinit var legendRecyclerAdapter: LegendRecyclerAdapter
    private val dateRecyclerAdapter =
        DateRecyclerAdapter(SimpleDateFormat("MMMM yyyy"), Calendar.MONTH)
    private var dateRecyclerViewPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthlyStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        legendRecyclerAdapter = LegendRecyclerAdapter(
            viewModel::addCategoryFilter,
            viewModel::removeCategoryFilter
        )
        binding.apply {
            recyclerViewDate.adapter = dateRecyclerAdapter
            recyclerViewDate.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val position = recyclerView.getCenterXChildPosition()
                    if (position != dateRecyclerViewPosition) {
                        viewModel.fetchData(dateRecyclerAdapter.getCurrentDate())
                        dateRecyclerViewPosition = position
                    }
                }
            })
            textViewTotal.setOnClickListener {
                showAmountDialog(textViewTotal.text.split(" ")[0].toDouble())
            }
            textViewMin.setOnClickListener {
                showAmountDialog(textViewMin.text.split(" ")[0].toDouble())
            }
            textViewMax.setOnClickListener {
                showAmountDialog(textViewMax.text.split(" ")[0].toDouble())
            }
            arrowLeft.setOnClickListener {
                recyclerViewDate.smoothScrollToPosition(recyclerViewDate.getCenterXChildPosition() - 1)
            }
            arrowRight.setRight()
            arrowRight.setOnClickListener {
                recyclerViewDate.smoothScrollToPosition(recyclerViewDate.getCenterXChildPosition() + 1)
            }
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            expensesChartView.description.text = ""
            textViewNoExpenses.visibility = View.GONE
            pieChart.legend.isEnabled = false
            pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.milky_white))
            pieChart.description.text = ""
            pieChart.description.textSize *= 1.5f
            pieChart.setDrawEntryLabels(false)
            pieChart.isDrawHoleEnabled = false
            recyclerViewLegend.layoutManager = object : LinearLayoutManager(requireContext(), VERTICAL, false){
                override fun canScrollVertically() = false
            }
            constraintLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            recyclerViewLegend.adapter = legendRecyclerAdapter
        }


        subscribeOnLiveData()

    }

    private fun showAmountDialog(amount: Double){
        AmountInSecondaryCurrenciesDialog(viewModel.getDataWrapper(), this).show(amount)
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            totalLiveData.observe(viewLifecycleOwner) {
                binding.textViewTotal.text = it
            }
            maxLiveData.observe(viewLifecycleOwner) {
                binding.textViewMax.text = it
            }
            minLiveData.observe(viewLifecycleOwner) {
                binding.textViewMin.text = it
            }
            lineChartStatisticsLiveData.observe(viewLifecycleOwner) {
                binding.expensesChartView.apply {
                    lineData = it
                    notifyDataSetChanged()
                    invalidate()
                    animateXY(600, 1000)
                }
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
}