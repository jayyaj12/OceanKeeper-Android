package com.letspl.oceankeeper.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankeeper.data.dto.ComingScheduleItem
import com.letspl.oceankeeper.databinding.ViewpagerItemComingScheduleBinding

class MainComingScheduleAdapter(private val item: List<ComingScheduleItem>, private val onClickedItem: (String) -> Unit) :
    RecyclerView.Adapter<MainComingScheduleAdapter.ComingScheduleViewPagerHolder>() {

    inner class ComingScheduleViewPagerHolder(private val binding: ViewpagerItemComingScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ComingScheduleItem) {
            binding.comingDateTv.text = "D-${item.dday}" // 다가오는 남은 Dday
            binding.titleTv.text = item.title // 제목
            binding.startTimeTv.text = item.startDay // 시작 시간
            binding.locationTv.text = item.location // 위치

            binding.itemCl.setOnClickListener {
                onClickedItem(item.id)
            }
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