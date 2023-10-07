package com.expenses.mngr.presentation.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.expenses.mngr.databinding.DialogColorPickerBinding
import com.expenses.mngr.extensions.hideSystemUI
import com.expenses.mngr.extensions.randomColor
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlin.random.Random

class ColorPickerDialog(private val activity: Activity, private val onColorPicked: (color: Int) -> Unit) : Dialog(activity) {

    private lateinit var binding: DialogColorPickerBinding
    private var boolVar0 = true
    private var boolVar1 = true

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DialogColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(
            InsetDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                ), 20, 0, 20, 130
            )
        )
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        with(binding) {
            colorPickerView.setColorListener(ColorEnvelopeListener { envelope, _ ->
                envelope?.let {
                    boolVar0 = false
                    if (boolVar1) {
                        val pos = editText.selectionStart
                        editText.setText(it.hexCode.substring(2))
                        editText.setSelection(pos)
                    } else boolVar1 = true
                    imageView.setBackgroundColor(it.color)
                }
            })
            colorPickerView.setInitialColor(Random(System.currentTimeMillis()).randomColor())
            colorPickerView.attachBrightnessSlider(brightnessSlide)
            editText.addTextChangedListener { editable ->
                val text = editable.toString()
                if (text.length == 6 && boolVar0) {
                    boolVar1 = false
                    val color = Color.parseColor("#$text")
                    colorPickerView.setInitialColor(color)
                    imageView.setBackgroundColor(color)
                } else boolVar0 = true
            }
            buttonConfirm.setOnClickListener {
                onColorPicked(colorPickerView.color)
                dismiss()
            }
            buttonCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onStop() {
        activity.hideSystemUI()
    }
}