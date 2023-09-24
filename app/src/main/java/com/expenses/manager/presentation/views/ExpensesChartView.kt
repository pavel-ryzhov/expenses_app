package com.expenses.manager.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.expenses.manager.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ExpensesChartView(context: Context, attributeSet: AttributeSet) :
    LineChart(context, attributeSet) {

    private var color: Int = -1

    init {
        setDrawBorders(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        axisRight.setDrawGridLines(false)
        legend.isEnabled = false
        xAxis.enableGridDashedLine(5f, 5f, 0f)
        axisLeft.enableGridDashedLine(5f, 5f, 0f)
        viewPortHandler.apply {
            setMaximumScaleX(3f)
            setMaximumScaleY(3f)
        }
        setNoDataText(context.getString(R.string.loading_data))
        setColor(R.color.blue)
    }

    fun setLineDataSet(lineDataSet: LineDataSet) {
        setLineDataSetParams(lineDataSet)
        if (lineDataSet.yMax != 0f)
            data = LineData(lineDataSet)
        else
            setNoDataText(context.getString(R.string.there_are_no_expenses_yet))
        notifyDataSetChanged()
        invalidate()
    }

    private fun setLineDataSetParams(lineDataSet: LineDataSet) {
        lineDataSet.apply {
            color = color
            setCircleColor(color)
            valueTextColor = color
            setDrawHighlightIndicators(false)
        }
    }

    fun setLineData(lineData: LineData) {
        lineData.dataSets.forEach {
            (it as LineDataSet).apply {
                circleHoleColor = ContextCompat.getColor(context, R.color.milky_white)
                setDrawHighlightIndicators(false)
            }
        }
        if (lineData.yMax != 0f)
            data = lineData
        else {
            data = null
            setNoDataText(context.getString(R.string.there_are_no_expenses_yet))
        }
        notifyDataSetChanged()
        invalidate()
    }

    fun setColor(@ColorRes colorRes: Int) {
        this.color = ContextCompat.getColor(context, colorRes)
        setBorderColor(color)
        axisLeft.gridColor = color
        xAxis.gridColor = color
        axisLeft.axisLineColor = color
        xAxis.axisLineColor = color
        axisLeft.textColor = color
        axisRight.textColor = color
        xAxis.textColor = color
        description.textColor = color
        setNoDataTextColor(color)
        if (data != null) {
            data.dataSets.forEach {
                (it as LineDataSet).apply {
                    this.color = this@ExpensesChartView.color
                    setCircleColor(this@ExpensesChartView.color)
                    valueTextColor = this@ExpensesChartView.color
                }
            }
        }
    }

}