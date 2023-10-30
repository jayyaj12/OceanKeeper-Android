package com.letspl.oceankepper.ui.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.dto.GetNoticeListDto
import com.letspl.oceankepper.data.dto.GetUserActivityListDto
import com.letspl.oceankepper.databinding.ItemApplyActivityBinding
import com.letspl.oceankepper.databinding.ItemNoticeBinding

class ApplyActivityListAdapter(private val context: Context) :
    ListAdapter<GetUserActivityListDto, ApplyActivityListAdapter.ApplyActivityViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<GetUserActivityListDto>() {
            override fun areItemsTheSame(
                oldItem: GetUserActivityListDto, newItem: GetUserActivityListDto
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetUserActivityListDto, newItem: GetUserActivityListDto
            ): Boolean {
                return oldItem.activityId == newItem.activityId
            }
        }
    }

    inner class ApplyActivityViewHolder(private val binding: ItemApplyActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: GetUserActivityListDto) {
            Glide.with(context).load(item.activityImageUrl).into(binding.thumbnailIv)
            binding.titleTv.text = item.title
            binding.applyCountTv.text = "${item.participants}/${item.quota}"
            binding.hostTv.text = "모집키퍼: ${item.hostNickname}"
            binding.nowApplyCountTv.text = "신청현황: ${
                if (item.participants < 10) {
                    "0${item.participants}"
                } else {
                    item.participants
                }
            }/${item.quota}명"
            binding.placeTv.text = "활동장소: ${item.address}"
            binding.dateTv.text = "모집기간: ${item.recruitStartAt.substring(2, item.recruitStartAt.length).replace("-", ".")}~${item.recruitEndAt.substring(2, item.recruitEndAt.length).replace("-", ".")}"
            binding.timeTv.text = "활동시작: ${item.startAt.substring(2, 10).replace("-", ".")} ${item.startAt.substring(12, 15).replace("-", ".")}시"

            showBtn(item.status)
        }

        // 활동별에 맞는 버튼을 표시함
        private fun showBtn(status: String) {
            when(status) {
                "OPEN" -> {
                    binding.editApplyTv.visibility = View.VISIBLE
                    binding.cancelApplyTv.visibility = View.VISIBLE
                }
                "CANCEL" -> {
                    binding.rejectApplyTv.visibility = View.VISIBLE
                }
                "CLOSe" -> {
                    binding.confirmApplyTv.visibility = View.VISIBLE
                }
            }
        }

        // 마지막 뷰의 경우 하단에 10dp에 해당하는 여백을 가진다.
        fun showLastView() {
            binding.lastV.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplyActivityViewHolder {
        return ApplyActivityViewHolder(
            ItemApplyActivityBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ApplyActivityViewHolder, position: Int) {
        holder.onBind(currentList[position])

        if(position == currentList.size - 1) {
            holder.showLastView()
        }
    }

}