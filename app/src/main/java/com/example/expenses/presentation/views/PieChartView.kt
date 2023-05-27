package com.example.expenses.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.expenses.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

@SuppressLint("ClickableViewAccessibility")
class PieChartView(context: Context, attributeSet: AttributeSet) : PieChart(context, attributeSet){

    private var onEntryClickListener: OnEntryClickListener = object : OnEntryClickListener{
        override fun onEntryClick(entry: PieEntry) {
        }
    }

    fun setOnEntryClickListener(onEntryClickListener: OnEntryClickListener){
        this.onEntryClickListener = onEntryClickListener
    }

    init {
        this.setNoDataText("Loading data...")
        this.setNoDataTextColor(ContextCompat.getColor(context, R.color.milky_white))
        this.legend.isEnabled = false
        this.setHoleColor(ContextCompat.getColor(context, R.color.milky_white))
        this.description.text = ""
        this.description.textSize *= 1.5f
        this.setDrawEntryLabels(false)
        this.isRotationEnabled = false
        this.setOnTouchListener { _, _ ->
            highlightValues(null)
            false
        }
        this.setOnChartValueSelectedListener(object : OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                highlightValues(null)
                e?.let { onEntryClickListener.onEntryClick(e as PieEntry) }
            }

            override fun onNothingSelected() {
            }

        })
        this.isDrawHoleEnabled = false
    }

    interface OnEntryClickListener{
        fun onEntryClick(entry: PieEntry)
    }
}