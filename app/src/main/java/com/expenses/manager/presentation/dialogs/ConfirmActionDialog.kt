package com.expenses.manager.presentation.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.expenses.manager.databinding.DialogConfirmActionBinding
import com.expenses.manager.extensions.hideSystemUI

class ConfirmActionDialog(
    private val text: String,
    private val onConfirmed: () -> Unit = {},
    private val onCanceled: () -> Unit = {}
) : DialogFragment() {

    private lateinit var binding: DialogConfirmActionBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogConfirmActionBinding.inflate(layoutInflater).apply {
            textViewLabel.text = text
            buttonConfirm.setOnClickListener {
                onConfirmed()
                requireDialog().dismiss()
            }
            buttonCancel.setOnClickListener {
                requireDialog().cancel()
            }
        }
        return AlertDialog.Builder(requireContext()).setView(binding.root).create().apply {
            window?.setBackgroundDrawable(
                InsetDrawable(
                    ColorDrawable(
                        Color.TRANSPARENT
                    ), 20, 50, 20, 130
                )
            )
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        onCanceled()
    }

    override fun onStop() {
        requireActivity().hideSystemUI()
        super.onStop()
    }
}