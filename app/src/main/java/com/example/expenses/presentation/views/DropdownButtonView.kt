package com.example.expenses.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.expenses.R

class DropdownButtonView(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {

    private var dropped = false
    private var changingState = false
    private var onRolledUp: () -> Unit = {}
    private var onRolledDown: () -> Unit = {}
    private var animationTime = 400L
    private var animationStartTime = -1L
    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 4f
        color = ContextCompat.getColor(context, R.color.blue)
    }

    init {
        setOnClickListener {
            changeState()
        }
    }

    fun setAnimationTime(animationTime: Long) {
        this.animationTime = animationTime
    }

    fun setOnRolledUp(onRolledUp: () -> Unit) {
        this.onRolledUp = onRolledUp
    }

    fun setOnRolledDown(onRolledDown: () -> Unit) {
        this.onRolledDown = onRolledDown
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun setColorRes(@ColorRes color: Int) {
        setColor(ContextCompat.getColor(context, color))
    }

    private fun changeState() {
        changingState = true
        if (dropped) onRolledUp() else onRolledDown()
        dropped = !dropped
        animationStartTime = System.currentTimeMillis()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            val timeDifference = (System.currentTimeMillis() - animationStartTime)
            val extraY = if (timeDifference < animationTime) {
                if (dropped) timeDifference * height / 4f / animationTime else height / 4f - timeDifference * height / 4f / animationTime
            } else {
                changingState = false
                if (dropped) height / 4f else 0f
            }
            it.drawLine(
                width / 4f + 1,
                height * 3f / 8f + extraY,
                width / 2f + 1,
                height * 5f / 8f - extraY,
                paint
            )
            it.drawLine(
                width / 2f - 1,
                height * 5f / 8f - extraY,
                width * 3f / 4f - 1,
                height * 3f / 8f + extraY,
                paint
            )
        }
        if (changingState) invalidate()
    }
}