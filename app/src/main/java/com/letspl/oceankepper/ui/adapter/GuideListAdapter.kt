package com.letspl.oceankepper.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.data.dto.GuideItemDto
import com.letspl.oceankepper.databinding.ItemGuideBinding

class GuideListAdapter(private val onClicked: (String) -> Unit): ListAdapter<GuideItemDto, GuideListAdapter.GuideViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<GuideItemDto>() {
            override fun areItemsTheSame(oldItem: GuideItemDto, newItem: GuideItemDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: GuideItemDto, newItem: GuideItemDto): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class GuideViewHolder(val binding: ItemGuideBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: GuideItemDto) {
            binding.titleTv.text = item.title
            binding.dateTv.text = item.date.substring(0, 10).replace("-", ".")

            binding.clickIv.setOnClickListener {
                onClicked(item.videoId)
            }
        }

        fun disableLastLine() {
            binding.lineV.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        return GuideViewHolder(
            ItemGuideBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.onBind(currentList[position])

        // 마지막 라인은 줄 삭제
        if(currentList.size - 1 == position) {
            holder.disableLastLine()
        }
    }
}