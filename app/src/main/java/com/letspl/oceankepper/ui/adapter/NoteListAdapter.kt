package com.letspl.oceankepper.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.dto.NoteItemDto
import com.letspl.oceankepper.databinding.ItemNoteBinding
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import com.letspl.oceankepper.ui.viewmodel.NoteViewModel
import timber.log.Timber

class NoteListAdapter(private val noteViewModel: NoteViewModel): ListAdapter<NoteItemDto, NoteListAdapter.ViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<NoteItemDto>() {
            override fun areItemsTheSame(oldItem: NoteItemDto, newItem: NoteItemDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NoteItemDto, newItem: NoteItemDto): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class ViewHolder(val binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: NoteItemDto) {
            isRead(item.read)
            binding.apply {
                titleTv.text = item.title
                garbageCategoryTv.text = noteViewModel.getGarbageCategory(item.garbageCategory)
                timeTv.text = noteViewModel.convertIso8601ToCustomFormat(item.time)
            }
        }

        // 쪽지 아이콘 읽음 여부 처리
        private fun isRead(isRead: Boolean) {
            Timber.e("isRead $isRead")
            if(isRead) {
                binding.noteIconIv.setBackgroundResource(R.drawable.note_enable_icon)
            } else {
                binding.noteIconIv.setBackgroundResource(R.drawable.note_disable_icon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoteBinding.inflate(
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