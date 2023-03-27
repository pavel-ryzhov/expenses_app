package com.example.expenses.extensions

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.R
import kotlin.math.pow
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
    val childCount = childCount
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

fun NavController.navigateWithDefaultAnimation(@IdRes id: Int){
    navigate(id, null, getDefaultNavOptions())
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

fun Random.randomColor() = Color.rgb(nextInt(256), nextInt(256), nextInt(256))

//fun Int.toHexColor(): String{
//    var str = Integer.toHexString(this)
//    while (str.length < 6) str = "0$str"
//    return "#$str"
//}
