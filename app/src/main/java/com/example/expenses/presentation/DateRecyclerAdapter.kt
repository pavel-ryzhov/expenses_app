package com.example.expenses.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.expenses.databinding.DateRecyclerViewItemBinding
import com.example.expenses.extensions.getCenterXChildPosition
import com.example.expenses.extensions.toActivity
import com.example.expenses.presentation.dialogs.DatePickerDialog
import java.text.DateFormat
import java.util.*

class DateRecyclerAdapter(
    private val dateFormat: DateFormat,
    private val itemType: Int,
    private val itemCount: Int = 20,
    private val additionalItemCount: Int = 5,
) : RecyclerView.Adapter<DateRecyclerAdapter.Holder>() {
    private var calendar: Calendar = GregorianCalendar()
    private var status = 0
    private lateinit var recyclerView: RecyclerView
    private val list = mutableListOf<Calendar>()
    init {
        setDate(calendar)
    }

    fun getCurrentDate() = list[recyclerView.getCenterXChildPosition()]

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.scrollToPosition(itemCount / 2)
        PagerSnapHelper().attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = recyclerView.getCenterXChildPosition()
                if (needToInsertToStart(position)) addToStart()
                if (needToInsertToEnd(position)) addToEnd()
            }
        })
    }

    private fun needToInsertToStart(position: Int): Boolean {
        return position < itemCount / 2 - additionalItemCount
    }

    private fun needToInsertToEnd(position: Int): Boolean {
        return position > itemCount / 2 + additionalItemCount
    }

    private fun addToEnd() {
        status += additionalItemCount
        list.addAll(itemCount, MutableList(additionalItemCount) {
            (calendar.clone() as Calendar).apply {
                add(itemType, additionalItemCount + it + status)
            }
        })
        recyclerView.post { notifyItemRangeInserted(itemCount, additionalItemCount) }
        for (i in 0 until additionalItemCount) {
            list.removeAt(0)
        }
        recyclerView.post { notifyItemRangeRemoved(0, additionalItemCount) }
    }

    private fun addToStart() {
        status -= additionalItemCount
        list.addAll(0, MutableList(additionalItemCount) {
            (calendar.clone() as Calendar).apply {
                add(itemType, -itemCount / 2 + it + status)
            }
        })
        recyclerView.post { notifyItemRangeInserted(0, additionalItemCount) }
        for (i in 0 until additionalItemCount) {
            list.removeAt(itemCount)
        }
        recyclerView.post { notifyItemRangeRemoved(itemCount, additionalItemCount) }
    }

    fun setDate(calendar: Calendar) {
        this.calendar = calendar
        list.clear()
        list.addAll(List(itemCount){
            (calendar.clone() as Calendar).apply { add(itemType, -itemCount / 2 + it) }
        })
        status = 0
        if (::recyclerView.isInitialized)
            recyclerView.scrollToPosition(itemCount / 2)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            DateRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setDate(dateFormat.format(list[position].time))
    }

    override fun getItemCount() = itemCount

    inner class Holder(private val binding: DateRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setDate(string: String) {
            binding.textViewDate.text = string
            binding.textViewDate.setOnClickListener {
                recyclerView.context.toActivity().supportFragmentManager.let {
                    DatePickerDialog { calendar ->
                        setDate(calendar)
                    }.show(it, "")
                }
            }
        }
    }
}