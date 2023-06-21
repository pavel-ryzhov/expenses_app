package com.example.expenses.presentation.settings.manage_categories.add_category

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expenses.R
import com.example.expenses.databinding.FragmentAddCategoryBinding
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.extensions.navigateWithDefaultAnimation
import com.example.expenses.extensions.randomColor
import com.example.expenses.presentation.dialogs.ColorPickerDialog
import com.example.expenses.presentation.settings.manage_categories.choose_category_dialog.ChooseCategoryDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class AddCategoryFragment : Fragment() {

    companion object {
        const val PARENT_CATEGORY_NAME_TAG = "PARENT_CATEGORY_NAME_TAG"
    }

    private lateinit var binding: FragmentAddCategoryBinding
    private val viewModel by viewModels<AddCategoryViewModel>()

    private lateinit var parent: CategoryDBEntity
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
        with(binding){
            textInputLayoutParent.setEndIconOnClickListener {
                showChooseCategoryDialog()
            }
            autoCompleteTextViewParent.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    showChooseCategoryDialog()
                }
                true
            }
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonDone.setOnClickListener {
                viewModel.addCategory(parent, editTextName.text.toString(), color!!)
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

    private fun subscribeOnLiveData(){
        with(viewModel){
            colorAlreadyExistsLiveData.observe(viewLifecycleOwner){
                Toast.makeText(requireContext(), "There is already a category with this color!", Toast.LENGTH_SHORT).show()
            }
            invalidNameLiveData.observe(viewLifecycleOwner){
                binding.textInputLayoutName.apply {
                    error = it
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
            categoryAddedSuccessfullyLiveData.observe(viewLifecycleOwner){
                Toast.makeText(requireContext(), "Category added successfully.", Toast.LENGTH_LONG).show()
                requireActivity().onBackPressed()
            }
            defaultParentLiveData.observe(viewLifecycleOwner){
                parent = it
                binding.autoCompleteTextViewParent.setText(it.getShortName())
            }
        }
    }

    private fun showColorPickerDialog(){
        ColorPickerDialog(requireActivity()) {
            color = it
            binding.imageViewColor.setBackgroundColor(it)
        }.show()
    }

    private fun showChooseCategoryDialog() {
        ChooseCategoryDialog ({
            parent = it.toCategoryDBEntity()
            binding.autoCompleteTextViewParent.setText(it.name)
        }, showManageCategoriesOption = false).show(requireActivity().supportFragmentManager, null)
    }
}