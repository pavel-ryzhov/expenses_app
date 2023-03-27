package com.example.expenses.presentation

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.expenses.R
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar{
    companion object{
        fun makeAndShow(view: View, text: String, length: Int = Snackbar.LENGTH_LONG){
            Snackbar.make(
                view,
                text,
                length
            ).apply {
                this.view.layoutParams =
                    (this.view.layoutParams as FrameLayout.LayoutParams).apply {
                        gravity = Gravity.TOP.or(Gravity.CENTER_HORIZONTAL)
                        width = FrameLayout.LayoutParams.MATCH_PARENT
                    }
                this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setTextColor(ContextCompat.getColor(view.context, R.color.milky_white))
                }
                show()
            }
        }
    }
}