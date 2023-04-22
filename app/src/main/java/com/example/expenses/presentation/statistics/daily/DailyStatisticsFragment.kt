package com.example.expenses.presentation.statistics.daily

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.databinding.FragmentDailyStatisticsBinding
import com.example.expenses.extensions.getCenterXChildPosition
import com.example.expenses.presentation.DateRecyclerAdapter
import com.example.expenses.presentation.statistics.monthly.LegendRecyclerAdapter
import com.example.expenses.presentation.value_formatters.TimeValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DailyStatisticsFragment : Fragment() {

    private lateinit var binding: FragmentDailyStatisticsBinding
    private val viewModel: DailyStatisticsViewModel by viewModels()
    private val dateRecyclerAdapter = DateRecyclerAdapter(SimpleDateFormat("MMMM d"), Calendar.DAY_OF_MONTH)
    private lateinit var legendRecyclerAdapter: LegendRecyclerAdapter
    private var dateRecyclerViewPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        legendRecyclerAdapter = LegendRecyclerAdapter(
            viewModel::addCategoryFilter,
            viewModel::removeCategoryFilter
        )
        binding.apply {
            arrowRight.setRight()
            recyclerViewDate.adapter = dateRecyclerAdapter
            recyclerViewDate.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val position = recyclerView.getCenterXChildPosition()
                    if (position != dateRecyclerViewPosition) {
                        viewModel.fetchData(dateRecyclerAdapter.getCurrentDate())
                        dateRecyclerViewPosition = position
                    }
                }
            })
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            arrowLeft.setOnClickListener {
                recyclerViewDate.smoothScrollToPosition(recyclerViewDate.getCenterXChildPosition() - 1)
            }
            arrowRight.setOnClickListener {
                recyclerViewDate.smoothScrollToPosition(recyclerViewDate.getCenterXChildPosition() + 1)
            }
            expensesChartView.viewPortHandler.setMaximumScaleX(32f)
            expensesChartView.xAxis.isGranularityEnabled = true
            expensesChartView.xAxis.granularity = 1f
            expensesChartView.xAxis.valueFormatter = TimeValueFormatter()
            expensesChartView.description.text = ""
            constraintLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            recyclerViewLegend.adapter = legendRecyclerAdapter
            recyclerViewLegend.layoutManager = object : LinearLayoutManager(requireContext(), VERTICAL, false){
                override fun canScrollVertically() = false
            }
        }

        subscribeOnLiveData()

    }

    private fun subscribeOnLiveData(){
        viewModel.apply {
            lineChartStatisticsLiveData.observe(viewLifecycleOwner){
                binding.expensesChartView.apply {
                    lineData = it
                    notifyDataSetChanged()
                    invalidate()
                    animateXY(600, 1000)
                }
            }
            legendLiveData.observe(viewLifecycleOwner){
                legendRecyclerAdapter.setRootCategory(it)
            }
        }
    }

}