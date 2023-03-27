package com.example.expenses

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt

class ColorUtils {
    companion object {

        fun chooseMoreSimilarColor(color: Int, color1: Int, color2: Int) =
            if (countDistance(color, color1) < countDistance(color, color2)) color1 else color2

        fun chooseLessSimilarColor(color: Int, color1: Int, color2: Int) =
            if (countDistance(color, color1) > countDistance(color, color2)) color1 else color2

        fun chooseMoreSimilarColorWithRes(
            context: Context,
            @ColorRes color: Int,
            @ColorRes color1: Int,
            @ColorRes color2: Int
        ): Int {
            val clr = ContextCompat.getColor(context, color)
            val clr1 = ContextCompat.getColor(context, color1)
            val clr2 = ContextCompat.getColor(context, color2)
            return chooseMoreSimilarColor(clr, clr1, clr2)
        }

        fun chooseLessSimilarColorWithRes(
            context: Context,
            @ColorRes color: Int,
            @ColorRes color1: Int,
            @ColorRes color2: Int
        ): Int {
            val clr = ContextCompat.getColor(context, color)
            val clr1 = ContextCompat.getColor(context, color1)
            val clr2 = ContextCompat.getColor(context, color2)
            return chooseLessSimilarColor(clr, clr1, clr2)
        }

        private fun countDistance(color1: Int, color2: Int): Double {
            return sqrt(
                (Color.alpha(color1) - Color.alpha(color2)).toDouble().pow(2)
                        + (Color.red(color1) - Color.red(color2)).toDouble().pow(2)
                        + (Color.green(color1) - Color.green(color2)).toDouble().pow(2)
                        + (Color.blue(color1) - Color.blue(color2)).toDouble().pow(2)
            )
        }
    }
}