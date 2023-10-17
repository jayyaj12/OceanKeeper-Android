package com.letspl.oceankepper.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.databinding.ItemCalendarCellBinding
import com.letspl.oceankepper.databinding.ItemReceiveCrewBinding
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruitViewModel
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import timber.log.Timber

class MessageReceiveCrewAdapter(private val messageViewModel: MessageViewModel): RecyclerView.Adapter<MessageReceiveCrewAdapter.CalendarViewHolder>() {

    private var nicknameArr = arrayListOf<MessageModel.MessageSpinnerCrewNicknameItem>()

    inner class CalendarViewHolder(private val binding: ItemReceiveCrewBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: MessageModel.MessageSpinnerCrewNicknameItem) {
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
        holder.onBind(nicknameArr[position])
    }

    override fun getItemCount(): Int {
        return nicknameArr.size
    }

    fun setNicknameArr(_nicknameArr: ArrayList<MessageModel.MessageSpinnerCrewNicknameItem>) {
        nicknameArr = _nicknameArr
        Timber.e("_nicknameArr $_nicknameArr")

        notifyDataSetChanged()
    }
}