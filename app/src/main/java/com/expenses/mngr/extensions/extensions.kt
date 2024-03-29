package com.expenses.mngr.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.expenses.mngr.R
import com.expenses.mngr.entities.exchange_rates.ExchangeRate
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

private val DEFAULT_NAV_OPTIONS = NavOptions.Builder()
    .setEnterAnim(R.anim.fragment_slide_in)
    .setExitAnim(R.anim.fragment_fade_out)
    .setPopEnterAnim(R.anim.fragment_fade_in)
    .setPopExitAnim(R.anim.fragment_slide_out)
    .build()

fun Activity.hideSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(
        window, findViewById(R.id.activity_main)
    ).apply {
        hide(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun RecyclerView.getCenterXChildPosition(): Int {
    val childCount = childCount
    if (childCount > 0) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (isChildInCenterX(child)) {
                return getChildAdapterPosition(child)
            }
        }
    }
    return childCount
}

private fun RecyclerView.isChildInCenterX(child: View): Boolean {
    val lvLocationOnScreen = IntArray(2)
    val vLocationOnScreen = IntArray(2)
    this.getLocationOnScreen(lvLocationOnScreen)
    val middleX = lvLocationOnScreen[0] + width / 2
    if (childCount > 0) {
        child.getLocationOnScreen(vLocationOnScreen)
        if (vLocationOnScreen[0] <= middleX
            && vLocationOnScreen[0] + child.width >= middleX
        ) {
            return true
        }
    }
    return false
}

fun getDefaultNavOptions() = DEFAULT_NAV_OPTIONS

fun NavController.navigateWithDefaultAnimation(@IdRes id: Int, args: Bundle? = null){
    navigate(id, args, getDefaultNavOptions())
}


fun Double.round(decimalNumbers: Int = 2): Double{
    val a = 10.0.pow(decimalNumbers)
    return kotlin.math.round(this * a) / a
}
fun Double.format(decimalNumbers: Int = 2): String{
    var str = this.toString()
    for (i in (str.length - str.indexOf('.') - 1) until decimalNumbers){
        str += "0"
    }
    return str
}

fun Double.roundAndFormat(decimalNumbers: Int = 2) = this.round(decimalNumbers).format(decimalNumbers)

fun Float.roundAndFormat(decimalNumbers: Int = 2) = this.toDouble().roundAndFormat(decimalNumbers)

fun Random.randomColor() = Color.rgb(nextInt(256), nextInt(256), nextInt(256))

fun Context.toActivity() = (if (this is ContextWrapper) this.baseContext else this) as AppCompatActivity

fun Double.roundWithAccuracy(accuracy: Int): Int = (this / accuracy).roundToInt() * accuracy

fun List<ExchangeRate>.find(currency: String): ExchangeRate? {
    this.forEach {
        if (it.code == currency) return it
    }
    return null
}

fun List<ExchangeRate>.toMap(): MutableMap<String, Float> {
    val result = mutableMapOf<String, Float>()
    this.forEach {
        result[it.code] = it.value
    }
    return result
}