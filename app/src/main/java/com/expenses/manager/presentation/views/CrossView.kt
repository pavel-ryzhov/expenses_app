package com.expenses.manager.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.expenses.manager.R

class CrossView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 5f
        color = ContextCompat.getColor(context, R.color.blue)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.drawLine(width / 3f, height / 3f, width * 2 / 3f, height * 2 / 3f, paint)
            it.drawLine(width * 2 / 3f, height / 3f, width / 3f, height * 2 / 3f, paint)
        }
    }
}