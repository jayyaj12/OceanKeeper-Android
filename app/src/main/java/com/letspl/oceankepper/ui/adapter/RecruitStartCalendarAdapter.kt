package com.letspl.oceankepper.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.databinding.ItemCalendarCellBinding
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruitViewModel

class  RecruitStartCalendarAdapter(private val activityRecruitViewModel: ActivityRecruitViewModel, private val onChangeDate: () -> Unit): RecyclerView.Adapter<RecruitStartCalendarAdapter.CalendarViewHolder>() {

    private var dateArr = arrayListOf<String>()

    inner class CalendarViewHolder(private val binding: ItemCalendarCellBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: String) {
            if(activityRecruitViewModel.getRecruitStartClickedDate() == activityRecruitViewModel.getRecruitStartDate().substring(0, 7) &&
                adapterPosition == activityRecruitViewModel.getRecruitStartDateClickPosition()) {
                binding.clickDateTv.visibility = View.VISIBLE
            } else {
                binding.clickDateTv.visibility = View.GONE
            }

            binding.dateTv.text = item
            binding.clickDateTv.text = item

            itemView.setOnClickListener {
                if(binding.dateTv.text != "") {
                    if(adapterPosition != -1) {
                        activityRecruitViewModel.setRecruitStartDateClickPosition(adapterPosition)
                        activityRecruitViewModel.setRecruitStartDateNowDate(dateArr[adapterPosition].toInt())
                        activityRecruitViewModel.setRecruitStartClickedDate(
                            activityRecruitViewModel.getRecruitStartDate().substring(0, 7)
                        )
                        onChangeDate()
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(
            ItemCalendarCellBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.onBind(dateArr[position])
    }

    override fun getItemCount(): Int {
        return dateArr.size
    }

    fun setDateArr(_dateArr: ArrayList<String>) {
        dateArr = _dateArr
    }
}