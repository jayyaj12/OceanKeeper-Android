package com.letspl.oceankepper.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.databinding.ViewpagerItemComingScheduleBinding

class MainComingScheduleAdapter(private val item: ArrayList<ComingScheduleItem>) :
    RecyclerView.Adapter<MainComingScheduleAdapter.ComingScheduleViewPagerHolder>() {

    class ComingScheduleViewPagerHolder(private val binding: ViewpagerItemComingScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ComingScheduleItem) {
            binding.comingDateTv.text = "D-${item.date}" // 다가오는 남은 Dday
            binding.titleTv.text = item.title // 제목
            binding.startTimeTv.text = item.startDate // 시작 시간
            binding.locationTv.text = item.location // 위치
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ComingScheduleViewPagerHolder {
        return ComingScheduleViewPagerHolder(
            ViewpagerItemComingScheduleBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ComingScheduleViewPagerHolder, position: Int) {
        holder.onBind(item[position])
    }

    override fun getItemCount(): Int {
        return item.size
    }

}