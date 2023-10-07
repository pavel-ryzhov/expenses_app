package com.expenses.mngr.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.expenses.mngr.R

class FilterView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 3f
        color = ContextCompat.getColor(context, R.color.blue)
    }

    fun setColor(id: Int) {
        paint.color = ContextCompat.getColor(context, id)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.drawLine(width / 2f, height * 3f / 4, width / 2f, height / 2f, paint)
            it.drawLine(width / 2f, height / 2f, width * 3f / 4, height / 4f, paint)
            it.drawLine(width * 3f / 4, height / 4f, width / 4f, height / 4f, paint)
            it.drawLine(width / 4f, height / 4f, width / 2f, height / 2f, paint)
        }
    }
}