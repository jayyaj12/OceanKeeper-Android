package com.letspl.oceankeeper.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankeeper.data.dto.GetGuideListDto
import com.letspl.oceankeeper.databinding.ItemGuideBinding

class GuideListAdapter(private val onClicked: (String, String) -> Unit): ListAdapter<GetGuideListDto, GuideListAdapter.GuideViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<GetGuideListDto>() {
            override fun areItemsTheSame(oldItem: GetGuideListDto, newItem: GetGuideListDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: GetGuideListDto, newItem: GetGuideListDto): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class GuideViewHolder(val binding: ItemGuideBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: GetGuideListDto) {
            binding.titleTv.text = item.title
            binding.dateTv.text = item.modifiedAt.substring(0, 10).replace("-", ".")

            binding.clickIv.setOnClickListener {
                onClicked(getVideoId(item.videoLink), item.videoName)
            }
        }

        fun disableLastLine() {
            binding.lineV.visibility = View.GONE
        }
    }

    // 유투브 재생에 필요한 videoId 를 videoLink url 로 부터 파싱함
    private fun getVideoId(url :String): String {
        return url.removePrefix("https://www.youtube.com/watch?v=").split("&")[0]
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