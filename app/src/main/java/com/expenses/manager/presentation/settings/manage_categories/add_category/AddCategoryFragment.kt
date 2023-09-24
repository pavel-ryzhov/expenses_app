package com.expenses.manager.presentation.settings.manage_categories.add_category

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.expenses.manager.R
import com.expenses.manager.databinding.FragmentAddCategoryBinding
import com.expenses.manager.extensions.randomColor
import com.expenses.manager.presentation.dialogs.ColorPickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class AddCategoryFragment : Fragment() {

    companion object {
        const val PARENT_CATEGORY_NAME_TAG = "PARENT_CATEGORY_NAME_TAG"
    }

    private lateinit var binding: FragmentAddCategoryBinding
    private val viewModel by viewModels<AddCategoryViewModel>()

    private var color: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonDone.setOnClickListener {
                viewModel.addCategory(editTextName.text.toString(), color!!)
            }
            textViewColorLabel.setOnClickListener {
                showColorPickerDialog()
            }
            imageViewColor.setOnClickListener {
                showColorPickerDialog()
            }
            Random(System.currentTimeMillis()).randomColor().also {
                this@AddCategoryFragment.color = it
                imageViewColor.setBackgroundColor(it)
            }
        }

        subscribeOnLiveData()

        viewModel.setDefaultParent(requireArguments().getString(PARENT_CATEGORY_NAME_TAG)!!)
    }

    private fun subscribeOnLiveData() {
        with(viewModel) {
            colorAlreadyExistsLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.there_is_already_a_category_with_this_color),
                    Toast.LENGTH_SHORT
                ).show()
            }
            invalidNameLiveData.observe(viewLifecycleOwner) {
                binding.textInputLayoutName.apply {
                    error = it
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
            categoryAddedSuccessfullyLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.category_added_successfully), Toast.LENGTH_LONG)
                    .show()
                requireActivity().onBackPressed()
            }
        }
    }

    private fun showColorPickerDialog() {
        ColorPickerDialog(requireActivity()) {
            color = it
            binding.imageViewColor.setBackgroundColor(it)
        }.show()
    }
}