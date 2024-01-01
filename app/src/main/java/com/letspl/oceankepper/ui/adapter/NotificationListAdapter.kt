package com.letspl.oceankepper.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.dto.MessageItemDto
import com.letspl.oceankepper.data.dto.NotificationItemDto
import com.letspl.oceankepper.databinding.ItemMessageBinding
import com.letspl.oceankepper.databinding.ItemNotificationBinding
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel

class NotificationListAdapter(): ListAdapter<NotificationItemDto, NotificationListAdapter.ViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<NotificationItemDto>() {
            override fun areItemsTheSame(oldItem: NotificationItemDto, newItem: NotificationItemDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NotificationItemDto, newItem: NotificationItemDto): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class ViewHolder(val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: NotificationItemDto) {
            isRead(item.read)

            binding.apply {
                contentsTv.text = item.contents
                timeTv.text = item.createAt
            }
        }

        // 쪽지 아이콘 읽음 여부 처리
        private fun isRead(isRead: Boolean) {
            if(isRead) {
                binding.notificationIconIv.setBackgroundResource(R.drawable.notification_gray)
            } else {
                binding.notificationIconIv.setBackgroundResource(R.drawable.notification_enable)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

}