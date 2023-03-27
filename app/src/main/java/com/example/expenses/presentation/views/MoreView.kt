package com.example.expenses.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.expenses.R

class MoreView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 4f
        color = ContextCompat.getColor(context, R.color.blue)
    }

    fun setColor(id: Int) {
        paint.color = ContextCompat.getColor(context, id)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            for (i in 0..2)
                it.drawLine(width / 3f, height / 3f + height / 6f * i, width / 3f * 2, height / 3f + height / 6f * i, paint)
        }
    }
}