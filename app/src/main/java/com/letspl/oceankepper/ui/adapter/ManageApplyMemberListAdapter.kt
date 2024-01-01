package com.letspl.oceankepper.ui.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.databinding.ItemApplyListBinding
import com.letspl.oceankepper.util.AllClickedStatus
import timber.log.Timber

class ManageApplyMemberListAdapter(private val isAllClicked: (AllClickedStatus) -> Unit, private val clickedCrewDetail: (String) -> Unit): ListAdapter<ManageApplyMemberModel.CrewInfoDto, ManageApplyMemberListAdapter.ManageApplyMemberViewHolder>(
    diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ManageApplyMemberModel.CrewInfoDto>() {
            override fun areItemsTheSame(
                oldItem: ManageApplyMemberModel.CrewInfoDto,
                newItem: ManageApplyMemberModel.CrewInfoDto,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ManageApplyMemberModel.CrewInfoDto,
                newItem: ManageApplyMemberModel.CrewInfoDto,
            ): Boolean {
                Timber.e("oldItem $oldItem")
                Timber.e("newItem $newItem")
                return oldItem.number == newItem.number && oldItem.isClicked == newItem.isClicked
            }
        }
    }

    inner class ManageApplyMemberViewHolder(private val binding: ItemApplyListBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ManageApplyMemberModel.CrewInfoDto) {
            binding.noTv.text = (adapterPosition + 1).toString()
            binding.nameTv.text = item.username
            binding.nicknameTv.text = item.nickname
            binding.nicknameTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            when(item.crewStatus) {
                "REJECT" -> {
                    binding.statusTv.text = "거절"
                    binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_disable)
                    setTextViewColor("#E0E0E0", "#E0E0E0", "#E0E0E0", "#E0E0E0")
                }
                "IN_PROGRESS" -> {
                    binding.statusTv.text = "승인"
                    binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_default)
                    setTextViewColor("#212121", "#212121", "#03A7B2", "#9E9E9E")
                }
                "NO_SHOW" -> {
                    binding.statusTv.text = "노쇼"
                    binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_default)
                    setTextViewColor("#212121", "#212121", "#03A7B2", "#9E9E9E")
                }
                "CLOSED" -> {
                    binding.statusTv.text = "활동종료"
                    binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_default)
                    setTextViewColor("#212121", "#212121", "#03A7B2", "#9E9E9E")
                }
            }

            Timber.e("item $item")
            if(item.crewStatus != "REJECT" && item.isClicked) {
                binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_checked)
            } else if(item.crewStatus != "REJECT" && !item.isClicked) {
                binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_default)
            }

            binding.nicknameTv.setOnClickListener {
                clickedCrewDetail(item.applicationId)
            }

            binding.checkBoxV.setOnClickListener {
                // 거절 상태일 경우 선택 불가
                if(item.crewStatus != "REJECT") {
                    // 눌렀을 때 이미 체크일 경우 체크 해제
                    if(item.isClicked) {
                        binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_default)
                        item.isClicked = false
                        ManageApplyMemberModel.applyCrewList[adapterPosition].isClicked = false
                    } else {
                        binding.checkBoxIv.setBackgroundResource(R.drawable.checkbox_checked)
                        item.isClicked = true
                        ManageApplyMemberModel.applyCrewList[adapterPosition].isClicked = true
                    }
                }

                isAllClicked(checkItemAllClicked())
            }
        }

        // 모두 클릭 됐는지 확인
        private fun checkItemAllClicked(): AllClickedStatus {
            var isClickedTrue = 0 // isClickedTrue 가 true 인 item 의 수
            var isClickedFalse = 0 // isClickedTrue 가 False 인 item 의 수
            var isReject = 0 // isReject 가 REJECT 인 item 의 수

            ManageApplyMemberModel.applyCrewList.forEachIndexed { index, crewInfoDto ->
                // crewStatus 가 REJECT 일 경우에는 totalCount 에서 제외한다.
                // 항상 false 이기 때문
                if(crewInfoDto.crewStatus == "REJECT") {
                    isReject++
                } else {
                    if(crewInfoDto.isClicked) {
                        // isClicked 가 true 일 경우에는 isClickedTrue 를 증가한다.
                        isClickedTrue++
                    } else {
                        isClickedFalse++
                    }
                }
            }

            // 전체 아이템 - reject Item 을 저장한다.
            val totalCount = ManageApplyMemberModel.applyCrewList.size - isReject

            // 전체 아이템의 수와 isClickedTrue 의 값을 비교하여
            // 같다면 전체 선택하기 On
            // 같지 않다면 전체 선택하기 Off
            return if(isClickedTrue == totalCount) {
                AllClickedStatus.AllClicked
            } else if(isClickedFalse == totalCount) {
                AllClickedStatus.NotAllClicked
            } else {
                AllClickedStatus.None
            }

        }

        private fun setTextViewColor(numberColor: String ,userNameColor: String, nicknameColor: String, statusColor:String) {
            binding.noTv.setTextColor(Color.parseColor(numberColor))
            binding.nameTv.setTextColor(Color.parseColor(userNameColor))
            binding.nicknameTv.setTextColor(Color.parseColor(nicknameColor))
            binding.statusTv.setTextColor(Color.parseColor(statusColor))
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