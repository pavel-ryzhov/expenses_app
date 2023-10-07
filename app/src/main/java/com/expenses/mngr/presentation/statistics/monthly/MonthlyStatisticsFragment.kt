package com.expenses.mngr.presentation.statistics.monthly

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expenses.mngr.R
import com.expenses.mngr.databinding.FragmentMonthlyStatisticsBinding
import com.expenses.mngr.extensions.getCenterXChildPosition
import com.expenses.mngr.presentation.DateRecyclerAdapter
import com.expenses.mngr.presentation.statistics.LegendRecyclerAdapter
import com.expenses.mngr.presentation.statistics.monthly.expenses_dialog.ExpensesDialog
import com.expenses.mngr.presentation.views.PieChartView
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MonthlyStatisticsFragment : Fragment() {

    private lateinit var binding: FragmentMonthlyStatisticsBinding
    private val viewModel by viewModels<MonthlyStatisticsViewModel>()
    private lateinit var legendRecyclerAdapter: LegendRecyclerAdapter
    @SuppressLint("SimpleDateFormat")
    private val dateRecyclerAdapter =
        DateRecyclerAdapter(SimpleDateFormat("MMMM yyyy", Locale.ENGLISH), Calendar.MONTH)
    private var dateRecyclerViewPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthlyStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
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
            buttonDaily.setOnClickListener {
                findNavController().navigate(R.id.action_monthlyStatisticsFragment_to_dailyStatisticsFragment)
            }
            buttonTotal.setOnClickListener {
                findNavController().navigate(R.id.action_monthlyStatisticsFragment_to_totalStatisticsFragment)
            }
            expensesChartView.description.text = ""
            pieChart.setOnEntryClickListener(object : PieChartView.OnEntryClickListener {
                override fun onEntryClick(entry: PieEntry) {
                    ExpensesDialog(entry.label, viewModel.calendar){
                        viewModel.fetchData(dateRecyclerAdapter.getCurrentDate())
                    }.show(requireActivity().supportFragmentManager, null)
                }
            })
            textViewNoExpenses.visibility = View.GONE
            recyclerViewLegend.layoutManager = object : LinearLayoutManager(requireContext(), VERTICAL, false){
                override fun canScrollVertically() = false
            }
            constraintLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            recyclerViewLegend.adapter = legendRecyclerAdapter
        }


        subscribeOnLiveData()

    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            totalLiveData.observe(viewLifecycleOwner) {
                binding.textViewTotal.setAmount(it)
            }
            lineChartStatisticsLiveData.observe(viewLifecycleOwner) {
                binding.expensesChartView.apply {
                    lineData = it
                    notifyDataSetChanged()
                    invalidate()
                    animateXY(300, 500)
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