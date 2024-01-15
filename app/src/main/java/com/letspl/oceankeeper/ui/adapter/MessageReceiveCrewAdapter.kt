package com.letspl.oceankeeper.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankeeper.data.model.MessageModel
import com.letspl.oceankeeper.databinding.ItemReceiveCrewBinding
import com.letspl.oceankeeper.ui.viewmodel.MessageViewModel
import timber.log.Timber

class MessageReceiveCrewAdapter(private val messageViewModel: MessageViewModel): RecyclerView.Adapter<MessageReceiveCrewAdapter.CalendarViewHolder>() {

    private var nicknameArr = arrayListOf<MessageModel.MessageSpinnerCrewNicknameItem>()

    inner class CalendarViewHolder(private val binding: ItemReceiveCrewBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: MessageModel.MessageSpinnerCrewNicknameItem) {
            Timber.e("MessageReceiveCrewAdapter item $item")
            if(item.isChecked) {
                binding.itemCl.setBackgroundColor(Color.parseColor("#eceff1"))
                binding.checkIconIv.visibility = View.VISIBLE
            } else {
                binding.checkIconIv.visibility = View.INVISIBLE
            }

            binding.nicknameTv.text = item.nickname

            binding.itemCl.setOnClickListener {
                if(item.isChecked) {
                    messageViewModel.removeReceiveList(item.nickname)
                    nicknameArr[adapterPosition].isChecked = false
                } else {
                    messageViewModel.addReceiveList(item.nickname)
                    nicknameArr[adapterPosition].isChecked = true
                }

                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(
            ItemReceiveCrewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        Timber.e("onBindViewHolder ${messageViewModel.getCrewList()}")
        holder.onBind(messageViewModel.getCrewList()[position])
    }

    override fun getItemCount(): Int {
        Timber.e("getItemCount ${messageViewModel.getCrewList().size}")
        return messageViewModel.getCrewList().size
    }

    fun setNicknameArr(_nicknameArr: ArrayList<MessageModel.MessageSpinnerCrewNicknameItem>) {
        nicknameArr = _nicknameArr
        Timber.e("nicknameArr $nicknameArr")
        notifyDataSetChanged()
    }
}