package com.letspl.oceankeeper.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.data.dto.MessageItemDto
import com.letspl.oceankeeper.databinding.ItemMessageBinding
import com.letspl.oceankeeper.ui.viewmodel.MessageViewModel

class MessageListAdapter(private val messageViewModel: MessageViewModel, private val onClickedMessage: (MessageItemDto) -> Unit): ListAdapter<MessageItemDto, MessageListAdapter.ViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MessageItemDto>() {
            override fun areItemsTheSame(oldItem: MessageItemDto, newItem: MessageItemDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MessageItemDto, newItem: MessageItemDto): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class ViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: MessageItemDto) {
            isRead(item.read)
            binding.apply {
                titleTv.text = item.activityTitle
                contentTv.text = item.messageBody
                fromTv.text = item.from
                timeTv.text = item.messageSentAt.replace("-", ".")
            }

            binding.moveDetailPageIv.setOnClickListener {
                onClickedMessage(item)
            }
        }

        // 쪽지 아이콘 읽음 여부 처리
        private fun isRead(isRead: Boolean) {
            if(isRead) {
                binding.noteIconIv.setBackgroundResource(R.drawable.note_disable_icon)
            } else {
                binding.noteIconIv.setBackgroundResource(R.drawable.note_enable_icon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMessageBinding.inflate(
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