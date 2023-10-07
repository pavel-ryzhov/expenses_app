package com.expenses.mngr.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("SimpleDateFormat")
fun formatTime(hour: Int, minute: Int): String {
    return DateFormat.getTimeInstance(DateFormat.SHORT)
        .format(SimpleDateFormat("H:m").parse("$hour:$minute")!!)
}

@SuppressLint("SimpleDateFormat")
fun formatDate(month: Int, day: Int): String {
    return SimpleDateFormat("MMMM d", Locale.ENGLISH).format(GregorianCalendar().apply {
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
    }.time)
}

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
        (Color.red(color1) - Color.red(color2)).toDouble().pow(2)
                + (Color.green(color1) - Color.green(color2)).toDouble().pow(2)
                + (Color.blue(color1) - Color.blue(color2)).toDouble().pow(2)
    )
}

fun isServiceRunning(context: Context, serviceClass: Class<out Service>): Boolean {
    for (service in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
        Int.MAX_VALUE
    )) {
        if (serviceClass.name == service.service.className) return true
    }
    return false
}