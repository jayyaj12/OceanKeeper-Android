package com.letspl.oceankepper.ui.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.dto.GetNoticeListDto
import com.letspl.oceankepper.data.dto.GetUserActivityListDto
import com.letspl.oceankepper.databinding.ItemApplyActivityBinding
import com.letspl.oceankepper.databinding.ItemNoticeBinding
import timber.log.Timber

class ApplyActivityListAdapter(private val context: Context, private val onClickEditApply: (String) -> Unit, private val onClickCancelApply: (String) -> Unit, private val onClickCheckRejectReason: (String) -> Unit) :
    ListAdapter<GetUserActivityListDto, ApplyActivityListAdapter.ApplyActivityViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<GetUserActivityListDto>() {
            override fun areItemsTheSame(
                oldItem: GetUserActivityListDto, newItem: GetUserActivityListDto
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetUserActivityListDto, newItem: GetUserActivityListDto
            ): Boolean {
                return oldItem.activityId == newItem.activityId
            }
        }
    }

    inner class ApplyActivityViewHolder(private val binding: ItemApplyActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: GetUserActivityListDto) {
            Glide.with(context).load(item.activityImageUrl).into(binding.thumbnailIv)
            binding.titleTv.text = item.title
            binding.applyCountTv.text = "${item.participants}/${item.quota}"
            binding.hostTv.text = "모집키퍼: ${item.hostNickname}"
            binding.nowApplyCountTv.text = "신청현황: ${
                if (item.participants < 10) {
                    "0${item.participants}"
                } else {
                    item.participants
                }
            }/${item.quota}명"
            binding.placeTv.text = "활동장소: ${item.address}"
            binding.dateTv.text = "모집기간: ${item.recruitStartAt.substring(2, item.recruitStartAt.length).replace("-", ".")}~${item.recruitEndAt.substring(2, item.recruitEndAt.length).replace("-", ".")}"
            binding.timeTv.text = "활동시작: ${item.startAt.substring(2, 10).replace("-", ".")} ${item.startAt.substring(11, 13)}시"

            showBtn(item.status, item.crewStatus)
            onClickBtn(item)
        }

        // 클릭 이벤트
        private fun onClickBtn(item: GetUserActivityListDto) {
            // 신청서 수정
            binding.editApplyTv.setOnClickListener {
                onClickEditApply(item.applicationId)
            }
            // 신청 취소
            binding.cancelApplyTv.setOnClickListener {
                onClickCancelApply(item.applicationId)
            }
            // 거절 사유 확인
            binding.rejectApplyTv.setOnClickListener {
                onClickCheckRejectReason(item.rejectReason)
            }
        }

        // 활동별에 맞는 버튼을 표시함
        private fun showBtn(status: String, crewStatus: String) {
            when(status) {
                "OPEN" -> {
                    when(crewStatus) {
                        "IN_PROGRESS" -> {
                            binding.countIv.visibility = View.VISIBLE
                            binding.editApplyTv.visibility = View.VISIBLE
                            binding.cancelApplyTv.visibility = View.VISIBLE
                        }
                        "CLOSED" -> {
                            binding.countIv.visibility = View.GONE
                            binding.applyCountTv.text = "활동 종료"
                        }
                        "REJECT" -> {
                            binding.rejectApplyTv.visibility = View.VISIBLE
                        }
                    }
                }
                // 활동 종료
                "CLOSED" -> {
                    binding.countIv.visibility = View.GONE
                    binding.applyCountTv.text = "활동 종료"
                }
                // 모집 종료
                "RECRUITMENT_CLOSE" -> {
                    binding.countIv.visibility = View.GONE
                    binding.editApplyTv.visibility = View.GONE
                    binding.cancelApplyTv.visibility = View.GONE
                    binding.applyCountTv.text = "모집 종료"
                }
            }

        }

        // 마지막 뷰의 경우 하단에 10dp에 해당하는 여백을 가진다.
        fun showLastView() {
            binding.lastV.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplyActivityViewHolder {
        return ApplyActivityViewHolder(
            ItemApplyActivityBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ApplyActivityViewHolder, position: Int) {
        holder.onBind(currentList[position])

        if(position == currentList.size - 1) {
            holder.showLastView()
        }
    }

}