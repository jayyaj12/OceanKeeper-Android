package com.letspl.oceankepper.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.letspl.oceankepper.data.dto.ActivityInfo
import com.letspl.oceankepper.data.dto.MyActivityItem
import com.letspl.oceankepper.databinding.ItemMainActivityBinding

class MainActivityListAdapter(private val context: Context, private val onClickedItem: (String) -> Unit) : ListAdapter<MyActivityItem, MainActivityListAdapter.ViewHolder>(diffUtil){

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MyActivityItem>() {
            override fun areItemsTheSame(oldItem: MyActivityItem, newItem: MyActivityItem): Boolean {
                return oldItem.activityImageUrl == newItem.activityImageUrl
            }

            override fun areContentsTheSame(oldItem: MyActivityItem, newItem: MyActivityItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(val binding: ItemMainActivityBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: MyActivityItem) {
            Glide.with(context)
                .load(item.activityImageUrl)
                .fitCenter()
                .into(binding.thumbnailIv)
            binding.recruitmentStatusTv.text = "${item.participants}/${item.quota}명"
            binding.nicknameTv.text = item.hostNickname
            binding.titleTv.text = item.title
            binding.locationTv.text = item.location
            binding.periodTv.text = "${item.recruitStartAt}~${item.recruitEndAt}"
            binding.timeTv.text = item.startAt

            binding.thumbnailIv.setOnClickListener {
                onClickedItem(item.activityId)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityListAdapter.ViewHolder {
        return ViewHolder(
            ItemMainActivityBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: MainActivityListAdapter.ViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

}