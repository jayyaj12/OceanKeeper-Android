package com.letspl.oceankepper.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.databinding.ItemApplyListBinding
import com.letspl.oceankepper.util.ApplyMemberStatus
import timber.log.Timber

class ManageApplyMemberListAdapter: ListAdapter<ManageApplyMemberModel.GetCrewInfoListDto, ManageApplyMemberListAdapter.ManageApplyMemberViewHolder>(
    diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ManageApplyMemberModel.GetCrewInfoListDto>() {
            override fun areItemsTheSame(
                oldItem: ManageApplyMemberModel.GetCrewInfoListDto,
                newItem: ManageApplyMemberModel.GetCrewInfoListDto,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ManageApplyMemberModel.GetCrewInfoListDto,
                newItem: ManageApplyMemberModel.GetCrewInfoListDto,
            ): Boolean {
                return oldItem.number == newItem.number
            }
        }
    }

    class ManageApplyMemberViewHolder(private val binding: ItemApplyListBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ManageApplyMemberModel.GetCrewInfoListDto) {
            binding.noTv.text = item.number.toString()
            binding.nameTv.text = item.username
            binding.nicknameTv.text = item.nickname
            when(item.crewStatus) {
                "REJECT" -> binding.statusTv.text = "거절"
                "CONFIRM" -> binding.statusTv.text = "승인"
                "NOSHOW" -> binding.statusTv.text = "노쇼"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageApplyMemberViewHolder {
        return ManageApplyMemberViewHolder(
            ItemApplyListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ManageApplyMemberViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
}