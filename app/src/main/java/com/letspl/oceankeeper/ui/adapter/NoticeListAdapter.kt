package com.letspl.oceankeeper.ui.adapter

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
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.data.dto.GetNoticeListDto
import com.letspl.oceankeeper.databinding.ItemNoticeBinding

class NoticeListAdapter : ListAdapter<GetNoticeListDto, NoticeListAdapter.NoticeViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<GetNoticeListDto>() {
            override fun areItemsTheSame(oldItem: GetNoticeListDto, newItem: GetNoticeListDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetNoticeListDto, newItem: GetNoticeListDto
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class NoticeViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun onBind(item: GetNoticeListDto) {
            binding.dateTv.text = item.modifiedAt.substring(0, 10).replace("-", ".")
            binding.titleTv.text = Html.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.contentTv.text = Html.fromHtml(item.contents, HtmlCompat.FROM_HTML_MODE_LEGACY)
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