package com.example.expenses.presentation.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.expenses.R
import com.example.expenses.extensions.hideSystemUI
import java.util.*

class DatePickerDialog(private val onDateSelected: (calendar: Calendar) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), R.style.DatePickerTheme, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        onDateSelected(GregorianCalendar(year, month, day))
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSystemUI()
    }
}