package com.example.expenses.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.expenses.R

class ArrowView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var isLeft = true
    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 5f
        color = ContextCompat.getColor(context, R.color.blue)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.drawLine(width / 4f, height / 2f, width / 4f * 3, height / 2f, paint)
            it.drawLine(
                if (isLeft) width / 4f else width / 4f * 3,
                height / 2f + 1,
                width / 2f,
                height / 4f + 1,
                paint
            )
            it.drawLine(
                if (isLeft) width / 4f else width / 4f * 3,
                height / 2f - 1,
                width / 2f,
                height / 4f * 3 - 1,
                paint
            )
        }
    }

    fun setLeft() {
        isLeft = true
    }

    fun setRight() {
        isLeft = false
    }

    fun setColor(id: Int) {
        paint.color = ContextCompat.getColor(context, id)
    }
}