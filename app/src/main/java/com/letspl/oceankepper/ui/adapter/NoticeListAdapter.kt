package com.letspl.oceankepper.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.NoticeItemDto
import com.letspl.oceankepper.databinding.ItemNoticeBinding

class NoticeListAdapter : ListAdapter<NoticeItemDto, NoticeListAdapter.NoticeViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<NoticeItemDto>() {
            override fun areItemsTheSame(oldItem: NoticeItemDto, newItem: NoticeItemDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: NoticeItemDto, newItem: NoticeItemDto
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class NoticeViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: NoticeItemDto) {
            binding.titleTv.text = item.title
            binding.dateTv.text = item.date.substring(0, 10).replace("-", ".")
            binding.contentTv.text = item.content
            binding.noticeClickIv.setBackgroundResource(R.drawable.notice_normal_icon)

            binding.noticeClickIv.setOnClickListener {
                if(binding.contentTv.isVisible) {
                    binding.contentTv.visibility = View.GONE
                    binding.noticeClickIv.setBackgroundResource(R.drawable.notice_normal_icon)
                } else {
                    binding.contentTv.visibility = View.VISIBLE
                    binding.noticeClickIv.setBackgroundResource(R.drawable.notice_open_icon)
                }
            }
        }

        fun disableLastLine() {
            binding.lineV.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        return NoticeViewHolder(
            ItemNoticeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.onBind(currentList[position])

        // 마지막 라인은 줄 삭제
        if(currentList.size - 1 == position) {
            holder.disableLastLine()
        }
    }

}