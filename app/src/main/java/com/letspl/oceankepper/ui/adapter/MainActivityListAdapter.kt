package com.letspl.oceankepper.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.letspl.oceankepper.data.dto.ActivityInfo
import com.letspl.oceankepper.databinding.ItemMainActivityBinding

class MainActivityListAdapter(private val context: Context) : ListAdapter<ActivityInfo, MainActivityListAdapter.ViewHolder>(diffUtil){

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ActivityInfo>() {
            override fun areItemsTheSame(oldItem: ActivityInfo, newItem: ActivityInfo): Boolean {
                return oldItem.thumbnailUrl == newItem.thumbnailUrl
            }

            override fun areContentsTheSame(oldItem: ActivityInfo, newItem: ActivityInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(val binding: ItemMainActivityBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: ActivityInfo) {
//            Glide.with(context)
//                .load(item.thumbnailUrl)
//                .fitCenter()
//                .into(binding.thumbnailIv)
            binding.recruitmentStatusTv.text = "${item.participants}/${item.period}명"
            binding.nicknameTv.text = item.nickname
            binding.titleTv.text = item.title
            binding.locationTv.text = item.location
            binding.periodTv.text = item.period
            binding.timeTv.text = item.time
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