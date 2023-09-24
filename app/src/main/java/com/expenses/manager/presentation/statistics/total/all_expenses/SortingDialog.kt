package com.expenses.manager.presentation.statistics.total.all_expenses

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.expenses.manager.R
import com.expenses.manager.databinding.DialogSortingBinding
import com.expenses.manager.entities.sorting.Sorting
import com.expenses.manager.extensions.hideSystemUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortingDialog(private val onSortingSelected: (sorting: Sorting) -> Unit, private val sorting: Sorting = Sorting.DATE) : DialogFragment() {

    private lateinit var binding: DialogSortingBinding
    private lateinit var autoCompleteTextViewSortingAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSortingBinding.inflate(inflater)
//        binding.recyclerView.adapter = adapter
        autoCompleteTextViewSortingAdapter = object :
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent)
                    .apply { textAlignment = View.TEXT_ALIGNMENT_CENTER }
            }
        }
        with(binding){
            autoCompleteTextViewSorting.setAdapter(autoCompleteTextViewSortingAdapter)
            autoCompleteTextViewSorting.setText(sorting.friendlyName, false)
            autoCompleteTextViewSorting.setDropDownBackgroundResource(R.color.milky_white)
            buttonOk.setOnClickListener {
                dismiss()
                onSortingSelected(Sorting.fromFriendlyName(autoCompleteTextViewSorting.text.toString())!!)
            }
        }
        autoCompleteTextViewSortingAdapter.addAll(Sorting.values().map { it.friendlyName })
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).setView(onCreateView(layoutInflater, null, savedInstanceState)).create().apply {
            window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 50, 0, 50, 300))
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        binding = DialogSortingBinding.inflate(layoutInflater)
//        window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20, 0, 20, 130))
//        window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        with(binding){
//            setContentView(root)
//            autoCompleteTextViewSorting.setAdapter(autoCompleteTextViewSortingAdapter)
//            autoCompleteTextViewSorting.setText(Sorting.DATE.friendlyName, false)
//            autoCompleteTextViewSorting.setDropDownBackgroundResource(R.color.milky_white)
//            buttonOk.setOnClickListener {
//                dismiss()
//                onSortingSelected(Sorting.fromFriendlyName(autoCompleteTextViewSorting.text.toString())!!)
//            }
//        }
//        autoCompleteTextViewSortingAdapter.addAll(Sorting.values().map { it.friendlyName })
//    }

    private fun subscribeOnLiveData(){

    }

    override fun onResume() {
        subscribeOnLiveData()
//        viewModel.fetchValues(amount)
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSystemUI()
    }
}